/**
 * Slack → Claude Code bridge.
 *
 * Listens for @-mentions in Slack channels (public or private) the bot is a
 * member of, spawns `claude -p` inside REPO_PATH, and posts the response back
 * into the originating thread. Each Slack thread maps to one Claude session,
 * so follow-up messages reuse --resume for conversation continuity.
 */

require('dotenv').config();
const { App, LogLevel } = require('@slack/bolt');
const { spawn } = require('node:child_process');
const fs = require('node:fs');
const path = require('node:path');

const {
  SLACK_APP_TOKEN,
  SLACK_BOT_TOKEN,
  REPO_PATH,
  CLAUDE_BIN = 'claude',
  CLAUDE_PERMISSION_MODE = 'plan',
  ALLOWED_USER_IDS = '',
} = process.env;

/**
 * System prompt appended to every Slack-originated Claude run.
 * Forces answers to stay at the business/product level so the channel
 * audience gets rules and flows, not file paths and code.
 */
const BUSINESS_STYLE_SYSTEM_PROMPT = [
  'You are answering questions in a Slack channel for a business audience (sales ops, product, support).',
  'Respond ONLY in business-logic terms: what the system does, the rules it enforces, the user-visible flows, and the decisions it makes.',
  'Do NOT include any of the following in your answer:',
  '- File paths, class names, method names, variable names, or table/column names',
  '- Code snippets, SQL, JSON, or configuration examples',
  '- Framework names, library names, or technology stack references',
  '- Line numbers, commit hashes, or repository structure',
  'Use plain language a non-engineer can act on. Prefer short bullet points and numbered steps over prose.',
  'If a question is purely technical (e.g. "what does this function return") and has no business-level answer, reply that the question is too technical for this channel and suggest rephrasing it around a user outcome.',
].join(' ');

// --- Config validation ---
for (const [k, v] of Object.entries({ SLACK_APP_TOKEN, SLACK_BOT_TOKEN, REPO_PATH })) {
  if (!v) {
    console.error(`Missing required env var: ${k}`);
    process.exit(1);
  }
}
if (!fs.existsSync(REPO_PATH)) {
  console.error(`REPO_PATH does not exist: ${REPO_PATH}`);
  process.exit(1);
}

const allowedUsers = new Set(
  ALLOWED_USER_IDS.split(',').map((s) => s.trim()).filter(Boolean),
);

// --- Session persistence (thread_ts → claude session id) ---
const SESSION_FILE = path.join(__dirname, 'sessions.json');
let threadSessions = {};
try {
  if (fs.existsSync(SESSION_FILE)) {
    threadSessions = JSON.parse(fs.readFileSync(SESSION_FILE, 'utf-8'));
  }
} catch (err) {
  console.warn('Could not load sessions.json, starting fresh:', err.message);
}
function saveSessions() {
  try {
    fs.writeFileSync(SESSION_FILE, JSON.stringify(threadSessions, null, 2));
  } catch (err) {
    console.warn('Could not persist sessions.json:', err.message);
  }
}

// --- Slack app (Socket Mode — no public URL needed) ---
const app = new App({
  token: SLACK_BOT_TOKEN,
  appToken: SLACK_APP_TOKEN,
  socketMode: true,
  logLevel: LogLevel.INFO,
});

// Strip leading <@BOTID> mention from the message text
function stripMention(text) {
  return text.replace(/^<@[^>]+>\s*/, '').trim();
}

/**
 * Run claude headless inside the repo. Returns { text, sessionId }.
 * Uses --resume <id> when we've seen this thread before.
 *
 * The prompt is written to stdin instead of passed via argv so we do not
 * have to fight cmd.exe quoting rules on Windows (which would otherwise
 * split the prompt on spaces and hand claude only the first word).
 */
function runClaude(prompt, priorSessionId) {
  return new Promise((resolve, reject) => {
    const args = [
      '-p',
      '--output-format', 'json',
      '--permission-mode', CLAUDE_PERMISSION_MODE,
    ];
    if (priorSessionId) args.push('--resume', priorSessionId);

    const proc = spawn(CLAUDE_BIN, args, {
      cwd: REPO_PATH,
      env: process.env,
      shell: process.platform === 'win32', // so claude.cmd resolves on Windows
      stdio: ['pipe', 'pipe', 'pipe'],
    });

    let stdout = '';
    let stderr = '';
    proc.stdout.on('data', (d) => { stdout += d.toString(); });
    proc.stderr.on('data', (d) => { stderr += d.toString(); });

    proc.on('error', reject);
    proc.on('close', (code) => {
      if (code !== 0) {
        return reject(new Error(`claude exited ${code}: ${stderr || stdout}`));
      }
      try {
        const parsed = JSON.parse(stdout);
        resolve({
          text: parsed.result ?? parsed.text ?? stdout,
          sessionId: parsed.session_id ?? parsed.sessionId ?? null,
        });
      } catch {
        resolve({ text: stdout, sessionId: null });
      }
    });

    // Prepend business-style instructions to the prompt via stdin.
    // Doing this via stdin (not --append-system-prompt argv) avoids
    // Windows cmd.exe re-splitting a multi-word argv on spaces.
    const framedPrompt =
      `INSTRUCTIONS FOR YOUR REPLY:\n${BUSINESS_STYLE_SYSTEM_PROMPT}\n\n` +
      `USER QUESTION:\n${prompt}`;
    proc.stdin.write(framedPrompt);
    proc.stdin.end();
  });
}

// --- Mention handler ---
app.event('app_mention', async ({ event, client, logger }) => {
  const userId = event.user;
  const channel = event.channel;
  const threadTs = event.thread_ts || event.ts;
  const promptText = stripMention(event.text || '');

  if (allowedUsers.size > 0 && !allowedUsers.has(userId)) {
    logger.warn(`Rejected mention from unauthorized user ${userId}`);
    await client.chat.postMessage({
      channel,
      thread_ts: threadTs,
      text: `Sorry <@${userId}>, you're not on the allowlist for this bot.`,
    });
    return;
  }

  if (!promptText) {
    await client.chat.postMessage({
      channel,
      thread_ts: threadTs,
      text: 'What would you like me to do? Mention me with a prompt.',
    });
    return;
  }

  // Ack immediately so the user sees activity
  const ack = await client.chat.postMessage({
    channel,
    thread_ts: threadTs,
    text: ':hourglass_flowing_sand: Working on it...',
  });

  const priorSession = threadSessions[threadTs];
  try {
    const { text, sessionId } = await runClaude(promptText, priorSession);
    if (sessionId) {
      threadSessions[threadTs] = sessionId;
      saveSessions();
    }
    // Slack message limit is 40k chars; truncate defensively
    const body = text.length > 38000 ? text.slice(0, 38000) + '\n\n…(truncated)' : text;
    await client.chat.update({
      channel,
      ts: ack.ts,
      text: body || '(no output)',
    });
  } catch (err) {
    logger.error(err);
    await client.chat.update({
      channel,
      ts: ack.ts,
      text: `:x: Claude failed: \`${err.message}\``,
    });
  }
});

(async () => {
  await app.start();
  console.log(`[OK] slack-claude-bot running (repo=${REPO_PATH}, mode=${CLAUDE_PERMISSION_MODE})`);
})();
