# slack-claude-bot

A Slack bot that forwards @-mentions to Claude Code running inside this repo.
Uses Slack Socket Mode — no public URL, no ngrok.

## Security first

**Never commit `.env`.** Tokens go there, and `.gitignore` excludes it.
If a token is ever pasted or leaked, rotate it immediately:
- App-level token: https://api.slack.com/apps → your app → Basic Information → App-Level Tokens
- Bot token: OAuth & Permissions → Reinstall to Workspace

## Slack app setup (one time)

1. Create an app at https://api.slack.com/apps → **From scratch**.
2. **Socket Mode** → enable → generate App-Level Token with `connections:write` scope → this is your `xapp-` token.
3. **OAuth & Permissions** → Bot Token Scopes:
   - `app_mentions:read`
   - `chat:write`
   - `channels:history` (public channels)
   - `groups:history` (private channels — add if you want private channel support)
4. **Event Subscriptions** → Enable Events → Subscribe to bot events: `app_mention`.
5. **Install to Workspace** → copy `xoxb-` bot token.
6. In Slack, `/invite @your-bot` into the channel you want it to listen in (private channels require explicit invite).

## Local run

```bash
cd scripts/slack-claude-bot
cp .env.example .env
# Fill in SLACK_APP_TOKEN, SLACK_BOT_TOKEN, ALLOWED_USER_IDS
npm install
npm start
```

Then in Slack: `@your-bot what's in docs/api/rest-endpoints.md?`

## How it works

- Bot uses Socket Mode (outbound WebSocket) — works behind any firewall.
- On `app_mention`, spawns `claude -p "<prompt>" --output-format json` with `cwd = REPO_PATH`.
- Each Slack thread maps to one Claude session in `sessions.json`; follow-ups in the same thread use `--resume <session-id>` so Claude remembers context.
- Response is posted back into the thread.

## Permission modes

Set `CLAUDE_PERMISSION_MODE` in `.env`:

| Mode | Behavior |
|------|----------|
| `plan` | **default, safest.** Claude plans but will not edit files. |
| `acceptEdits` | Claude can edit files in `REPO_PATH` without prompting. |
| `bypassPermissions` | All tools auto-approved. Only for fully trusted channels. |

## Allowlist

`ALLOWED_USER_IDS` in `.env` — comma-separated Slack user IDs (e.g. `U0123,U0456`). Leave empty to allow anyone in channels the bot is in (not recommended).

Find your Slack user ID: profile → ⋮ → Copy member ID.

## Caveats

- Long-running process — run under `pm2`, `nssm`, or Windows Task Scheduler for persistence.
- Concurrency: requests are serialized per thread via `sessions.json`; parallel threads run concurrently (each spawns its own `claude` process).
- Cost: each mention triggers a real Claude run. Keep the allowlist tight.
- `sessions.json` is local state only — deleting it loses thread→session mapping but not Slack history.
