// Copyright (C) 2026  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
(async () => {
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

function extractAllTextContentMatchGroup1(elemSelector, regex) {
    const elems = document.querySelectorAll(elemSelector);
    const matches = [];
    for (const elem of elems) {
        const match = elem.textContent.match(regex);
        if (match) {
            matches.push(match[1]);
        }
    }
    return matches.length > 0 ? matches : null;
 }

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

// in individual email, labels matching regex pattern
const extractedTextContentArr = extractAllTextContentMatchGroup1('div.ahR > span > div > div.hN', /^p\/\d{2}\/(\d{6,})$/);
if (!extractedTextContentArr) {
    const msg = 'Could not find search terms.';
    autoAlert(msg);
    throw new Error(msg);
}
const searchTermArr = [];
extractedTextContentArr.forEach(e => {
    searchTermArr.push(`^${e} `);
});
const script = `c.select(node.at(':~search')); node['patternArrJson'] = '${JSON.stringify(searchTermArr)}'; menuUtils.executeMenuItems(['ExecuteScriptForSelectionAction'])`;
autoAlert(await transfer(script), 2000);
})().catch(console.error);
// ### script1 at the node ':~search' ###
// import groovy.json.JsonSlurper
// def me = ID_461297460  // node.at(':~search')
// me.children.each { it.delete() }
// def firstResult = null
// def patternArrJson = me['patternArrJson'].text
// def patternList = new JsonSlurper().parseText(patternArrJson)
// patternList.each { pattern ->
//     def searchNode = me.createChild(pattern)
//     def rx = ~pattern
//     def projectsRoot = ID_222565764  // me.at(':~p')
//     projectsRoot.find { (it.text =~ rx) as Boolean }.each {
//         def n = searchNode.appendAsCloneWithoutSubtree(it)
//         if (!firstResult) firstResult = n
//     }
// }
// if (firstResult) c.select(firstResult)
