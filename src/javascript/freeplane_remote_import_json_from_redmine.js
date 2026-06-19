// Copyright (C) 2026  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
(async () => {
const redmineUrl = 'http://127.0.0.1:3020';
const deriveCoreName = issue => issue.assignedTo?.name && issue.assignedTo.name !== 'NameToAvoid' ? issue.assignedTo.name : issue.author.name;
const coreCustomFieldId = 0;
const redmineIssueRegex = new RegExp(`^${redmineUrl}/issues/([0-9]+)`);
function transfer(script) {
    return new Promise((resolve, reject) => {
        const ws = new WebSocket('ws://127.0.0.1:48113');
        ws.addEventListener('open', () => {
            ws.send(script);
        });
        const timeout = setTimeout(() => {
            ws.close();
            reject(new Error('WebSocket response timeout'));
        }, 10000);
        ws.addEventListener('message', (event) => {
            clearTimeout(timeout);
            console.log('Response:', event.data);
            ws.close();
            resolve(event.data);
        });
        ws.addEventListener('error', (event) => {
            clearTimeout(timeout);
            console.error('WebSocket error:', event);
            reject(new Error('WebSocket error'));
        });
        ws.addEventListener('close', (event) => {
            if (!event.wasClean) {
                clearTimeout(timeout);
                reject(new Error(`WebSocket closed unexpectedly: code=${event.code}`));
            }
        });
    });
}
function autoAlert(message, durationMs = 5000) {
    const overlay = document.createElement('div');
    overlay.style.cssText = `
font-family: Lato, Roboto, Arial, sans-serif;
position: fixed; top: 20px; left: 50%; transform: translateX(-50%);
background: #333; color: #fff; padding: 16px 32px;
border: 4px solid #ee5500;
border-radius: 8px; font-size: 16px; z-index: 9999;
box-shadow: 0 4px 12px rgba(0,0,0,0.3);
transition: opacity 0.3s ease;
overflow: hidden;`;
    message.split('\n').forEach((line, i) => {
        if (i > 0) overlay.appendChild(document.createElement('br'));
        overlay.appendChild(document.createTextNode(line));
    });
    const bar = document.createElement('div');
    bar.style.cssText = `
position: absolute; bottom: 0; left: 0;
height: 2px; width: 100%; background: #ee5500;
animation: _shrink ${durationMs}ms linear forwards;`;
    if (!document.getElementById('_autoAlertStyle')) {
        const style = document.createElement('style');
        style.id = '_autoAlertStyle';
        style.textContent = `@keyframes _shrink { from { width: 100% } to { width: 0% } }`;
        document.head.appendChild(style);
    }
    overlay.appendChild(bar);
    document.body.appendChild(overlay);
    const timer = setTimeout(() => {
        overlay.style.opacity = '0';
        setTimeout(() => overlay.remove(), 300);
    }, durationMs);
    const btn = document.createElement('span');
    btn.textContent = '×';
    btn.title = 'Close (Esc)';
    btn.style.cssText = 'position:absolute; top:4px; right:8px; cursor:pointer; font-weight:bold; padding:0 4px 0 4px; border: 1px solid #ee5500;';
    const dismiss = () => { clearTimeout(timer); overlay.remove(); document.removeEventListener('keydown', onKey); };
    const onKey = e => { if (e.key === 'Escape') dismiss(); };
    btn.addEventListener('click', dismiss);
    document.addEventListener('keydown', onKey);
    overlay.appendChild(btn);
}
const match = location.href.match(redmineIssueRegex);
if (!match) {
    autoAlert('Run me while on an issue page in Redmine');
    return;
}
const issueId = match[1];
const apiKeyResp = await fetch(`${redmineUrl}/my/api_key`);
    if (!apiKeyResp.ok) throw new Error(`Failed to fetch API key: ${apiKeyResp.status}`);
    const apiKeyHtml = await apiKeyResp.text();
    const preEl = new DOMParser()
        .parseFromString(apiKeyHtml, 'text/html')
        .querySelector('#content pre');
if (!preEl) {
    const msg = 'Could not retrieve API key.\nAre you signed in to Redmine?';
    autoAlert(msg);
    throw new Error(msg);
}
const apiKey = preEl.textContent.trim();
const issueResp = await fetch(`${redmineUrl}/issues/${issueId}.json`, {
    headers: { 'X-Redmine-API-Key': apiKey }
});
if (!issueResp.ok) {
    const msg = `Failed to fetch issue:\n${issueResp.status}`;
    autoAlert(msg);
    throw new Error(msg);
}
function getCustomFieldValueById(issue, customFieldId) {
    for (const cf of issue.custom_fields) {
        if (cf.id === customFieldId) {
            return cf.value;
        }
    }
    return `${customFieldId}`;
}
const response = await issueResp.json();
const issue = response.issue;
const fp = {
    [`ID_${issueId}`]: {
        '@core': `${issueId}  [${deriveCoreName(issue)}]  ${getCustomFieldValueById(issue, coreCustomFieldId)}`,
        '@link': location.href,
        '@details': issue.subject,
        '@attributes': {[`proj.${(new Date()).toISOString().split('T')[0]}`]: issue.project.name},
    }
};
const fpJson = JSON.stringify(fp);
if (fpJson.length > 1_000_000) {
    autoAlert('Too much data!\nOver 1,000,000 characters.');
    return;
}
const toBase64 = str => btoa(unescape(encodeURIComponent(str)));
const jsonBase64 = toBase64(fpJson);
function splitIntoChunks(str, chunkSize = 65535) {
    const chunks = [];
    for (let i = 0; i < str.length; i += chunkSize) {
        chunks.push(str.slice(i, i + chunkSize));
    }
    return chunks;
}
let script = 'def sb = new StringBuilder()\n';
splitIntoChunks(jsonBase64, 65535).forEach(chunk => script += `sb.append('${chunk}')\n`);
script += 'c.select(io.github.macmarrum.freeplane.Import.fromJsonStringBase64(sb.toString(), node))';
autoAlert(await transfer(script), 2000);
})().catch(console.error);
