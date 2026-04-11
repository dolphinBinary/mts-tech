import React, { useEffect, useMemo, useState } from "react";

const PAGE_LINK_RE = /\[\[page:([a-zA-Z0-9-_]+)\]\]/g;
const TABLE_RE = /\[\[table:([a-zA-Z0-9-_]+)\]\]/g;
const AI_RE = /\/ии\[(https?:\/\/[^\]]+)\]/g;

const API = "http://localhost:8080/api";
const PAGE_ID = "home";
const USER_ID = `user-${Math.floor(Math.random() * 10000)}`;

export function App() {
  const [title, setTitle] = useState("Главная");
  const [text, setText] = useState("");
  const [version, setVersion] = useState(0);
  const [backlinks, setBacklinks] = useState([]);
  const [activeUsers, setActiveUsers] = useState([]);
  const [tableEmbed, setTableEmbed] = useState(null);
  const [menu, setMenu] = useState({ open: false, x: 0, y: 0, mode: "", payload: "" });
  const [aiResult, setAiResult] = useState("");

  useEffect(() => {
    const cached = localStorage.getItem(`wiki-cache-${PAGE_ID}`);
    if (cached) {
      const data = JSON.parse(cached);
      setTitle(data.title);
      setText(data.content);
      setVersion(data.version ?? 0);
    }

    fetch(`${API}/pages/${PAGE_ID}`).then((r) => r.json()).then((data) => {
      setTitle(data.title);
      setText(data.content);
      setVersion(data.version);
      setBacklinks(data.backlinks || []);
    });
  }, []);

  useEffect(() => {
    localStorage.setItem(`wiki-cache-${PAGE_ID}`, JSON.stringify({ title, content: text, version }));

    const handle = setTimeout(async () => {
      const response = await fetch(`${API}/pages/${PAGE_ID}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ title, content: text, version })
      });
      const data = await response.json();
      setVersion(data.version);
      setBacklinks(data.backlinks || []);
    }, 700);

    return () => clearTimeout(handle);
  }, [title, text]);

  useEffect(() => {
    const interval = setInterval(async () => {
      const response = await fetch(`${API}/pages/${PAGE_ID}/presence`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ userId: USER_ID })
      });
      const users = await response.json();
      setActiveUsers(users);
    }, 3000);
    return () => clearInterval(interval);
  }, []);

  const pageLinks = useMemo(() => [...text.matchAll(PAGE_LINK_RE)].map((m) => m[1]), [text]);
  const tableLinks = useMemo(() => [...text.matchAll(TABLE_RE)].map((m) => m[1]), [text]);
  const aiLinks = useMemo(() => [...text.matchAll(AI_RE)].map((m) => m[1]), [text]);

  const openSlashMenu = (event) => {
    if (event.key === "/") {
      setMenu({ open: true, x: 60, y: 250, mode: "slash", payload: "" });
    }
    if (event.ctrlKey && event.key === "/") {
      event.preventDefault();
      setMenu({ open: true, x: 60, y: 250, mode: "slash", payload: "" });
    }
  };

  const embedTable = async (tableId) => {
    const response = await fetch(`${API}/tables/embed`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ tableId })
    });
    setTableEmbed(await response.json());
  };

  const runAi = async (url, action) => {
    const response = await fetch(`${API}/ai/action`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ url, action })
    });
    const data = await response.json();
    setAiResult(`${data.result}\nОграничения: ${data.limitations}`);
    setMenu({ open: false, x: 0, y: 0, mode: "", payload: "" });
  };

  return (
    <div className="page">
      <h1>MWS Wiki Editor module</h1>
      <input className="title" value={title} onChange={(e) => setTitle(e.target.value)} />
      <textarea
        value={text}
        onChange={(e) => setText(e.target.value)}
        onKeyDown={openSlashMenu}
        placeholder={"Примеры:\n[[page:finance]]\n[[table:sales_2026]]\n/ии[https://example.com]"}
        rows={12}
      />

      <div className="status">
        <span>version: {version}</span>
        <span>локальный кэш: включен</span>
        <span>онлайн: {activeUsers.length} пользователей</span>
      </div>

      {menu.open && menu.mode === "slash" && (
        <div className="menu" style={{ left: menu.x, top: menu.y }}>
          <div className="menu-title">Slash menu</div>
          <div>Команды:</div>
          <code>[[table:table_id]] — вставить таблицу</code>
          <code>[[page:page_id]] — ссылка на страницу</code>
          <code>/ии[url] — AI меню</code>
        </div>
      )}

      <div className="chips">
        {tableLinks.map((id) => (
          <button key={id} onClick={() => embedTable(id)}>Вставить таблицу {id}</button>
        ))}
        {aiLinks.map((url) => (
          <button key={url} onClick={() => setMenu({ open: true, x: 60, y: 420, mode: "ai", payload: url })}>ИИ: {url}</button>
        ))}
      </div>

      {menu.open && menu.mode === "ai" && (
        <div className="menu" style={{ left: menu.x, top: menu.y }}>
          <button onClick={() => runAi(menu.payload, "shorten")}>Сократить</button>
          <button onClick={() => runAi(menu.payload, "rewrite")}>Переписать</button>
        </div>
      )}

      {tableEmbed && (
        <div className="card">
          <h3>{tableEmbed.tableName}</h3>
          <p>Источник: {tableEmbed.sourceUrl}</p>
          <table>
            <thead>
              <tr>{tableEmbed.columns.map((c) => <th key={c}>{c}</th>)}</tr>
            </thead>
            <tbody>
              {tableEmbed.rows.map((row, i) => (
                <tr key={i}>{tableEmbed.columns.map((c) => <td key={c}>{String(row[c])}</td>)}</tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <div className="card">
        <h3>Backlinks</h3>
        {backlinks.length === 0 ? <p>Нет обратных ссылок</p> : backlinks.map((b) => <div key={b}>↩ {b}</div>)}
        <h4>Out links</h4>
        {pageLinks.map((p) => <div key={p}>→ {p}</div>)}
      </div>

      {aiResult && <pre className="result">{aiResult}</pre>}
    </div>
  );
}
