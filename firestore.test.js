import { test, before, after } from 'node:test';
import assert from 'node:assert';
import { readFileSync } from 'node:fs';

test('firestore.rules file exists and is valid syntax', () => {
  const rules = readFileSync('firestore.rules', 'utf8');
  assert.ok(rules.includes("rules_version = '2';"), 'Rules version 2 must be present');
  assert.ok(rules.includes('service cloud.firestore'), 'Service cloud.firestore must be present');
  assert.ok(rules.includes('match /databases/{database}/documents'), 'Documents match block present');
  assert.ok(rules.includes('match /{document=**}'), 'Default deny present');
  assert.ok(rules.includes('match /users/{userId}'), 'Users collection present');
  assert.ok(rules.includes('match /agents/{agentId}'), 'Agents collection present');
  assert.ok(rules.includes('match /sessions/{sessionId}'), 'Sessions collection present');
});
