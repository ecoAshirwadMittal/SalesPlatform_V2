// Loads + validates the parity manifest from docs/tasks/parity/ (single source of
// truth shared by humans and the harness — plan §4).
import * as fs from 'fs';
import * as path from 'path';
import * as yaml from 'js-yaml';
import { ConfigSchema, PageSchema, type ParityConfig, type ParityPage } from '../schema';

const DOCS_DIR = path.join(__dirname, '..', '..', '..', 'docs', 'tasks', 'parity');

export function loadConfig(): ParityConfig {
  const raw = yaml.load(fs.readFileSync(path.join(DOCS_DIR, 'config.yaml'), 'utf8'));
  return ConfigSchema.parse(raw);
}

export function loadPages(filter?: string): ParityPage[] {
  const dir = path.join(DOCS_DIR, 'pages');
  const pages = fs
    .readdirSync(dir)
    .filter((f) => f.endsWith('.yaml') || f.endsWith('.yml'))
    .map((f) => {
      try {
        return PageSchema.parse(yaml.load(fs.readFileSync(path.join(dir, f), 'utf8')));
      } catch (e) {
        throw new Error(`Invalid page manifest ${f}: ${e}`);
      }
    })
    .sort((a, b) => a.pageId.localeCompare(b.pageId));
  return filter ? pages.filter((p) => p.pageId === filter) : pages;
}
