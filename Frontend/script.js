(() => {
  const apiUrl = `${window.location.origin}/api`;
  const messagesEl = document.getElementById("chat-messages");
  const form = document.getElementById("chatForm");
  const input = document.getElementById("chatInput");
  const linkStatus = document.getElementById("linkStatus");
  const planetCards = document.getElementById("planetCards");
  const quickReplies = document.getElementById("quickReplies");

  const seedPlanets = ["kepler-442 b", "proxima centauri b", "k2-18 b"];
  const FETCH_MS = 15000;

  function appendMessage(html, sender) {
    const div = document.createElement("div");
    div.className = `message ${sender}`;
    if (sender === "bot") {
      div.innerHTML = html;
    } else {
      div.textContent = html;
    }
    messagesEl.appendChild(div);
    messagesEl.scrollTop = messagesEl.scrollHeight;
    return div;
  }

  function showTyping() {
    const typing = document.createElement("div");
    typing.className = "message bot typing-indicator";
    typing.innerHTML = "<span></span><span></span><span></span>";
    messagesEl.appendChild(typing);
    messagesEl.scrollTop = messagesEl.scrollHeight;
    return typing;
  }

  async function postChronos(message) {
    const controller = new AbortController();
    const timer = setTimeout(() => controller.abort(), FETCH_MS);
    try {
      const response = await fetch(apiUrl, {
        method: "POST",
        headers: { "Content-Type": "text/plain;charset=UTF-8" },
        body: message,
        signal: controller.signal,
      });
      if (!response.ok) {
        throw new Error("HTTP " + response.status);
      }
      return await response.text();
    } finally {
      clearTimeout(timer);
    }
  }

  async function sendToChronos(text) {
    const message = text.trim();
    if (!message) return;

    appendMessage(message, "user");
    const typing = showTyping();

    try {
      const reply = await postChronos(message);
      typing.remove();
      linkStatus.classList.add("online");
      appendMessage(reply, "bot");

      const lower = message.toLowerCase();
      if (lower.includes("planet") || lower.startsWith("exoplanet:") || lower.startsWith("analyze")) {
        upsertPlanetCard(message, reply);
      }
    } catch (err) {
      typing.remove();
      linkStatus.classList.remove("online");
      appendMessage("Link perdido com CHRONOS (timeout/rede). Confirme run.bat em :8080.", "bot");
      console.error(err);
    }
  }

  function upsertPlanetCard(query, rawHtml) {
    const titleMatch = rawHtml.match(/<strong>(.*?)<\/strong>/i);
    const name = titleMatch
      ? titleMatch[1]
      : query.replace(/analyze planet/i, "").replace(/exoplanet:/i, "").trim();
    const habitable = /zona habitável/i.test(rawHtml);
    const hostile = /demasiado|fora da zh/i.test(rawHtml);

    let card = [...planetCards.querySelectorAll(".planet-card")]
      .find((el) => el.dataset.planet === name);
    if (!card) {
      card = document.createElement("article");
      card.className = "planet-card";
      card.dataset.planet = name;
      planetCards.prepend(card);
    }
    card.classList.toggle("habitable", habitable);
    card.classList.toggle("hostile", hostile && !habitable);
    card.innerHTML = `
      <h3>${name}</h3>
      <div class="meta">${new Date().toLocaleTimeString()} · NASA TAP</div>
      <div class="body">${rawHtml}</div>
    `;
  }

  form.addEventListener("submit", (e) => {
    e.preventDefault();
    const value = input.value;
    input.value = "";
    sendToChronos(value);
  });

  quickReplies.addEventListener("click", (e) => {
    const btn = e.target.closest("button[data-cmd]");
    if (!btn) return;
    sendToChronos(btn.dataset.cmd);
  });

  // Paint UI immediately — do not wait on NASA
  appendMessage(
    "Sistema CHRONOS inicializado. Canal tático pronto. Use os atalhos ou digite um comando.",
    "bot"
  );
  linkStatus.classList.add("online");

  // Background telemetry (failures must not freeze the bridge)
  seedPlanets.forEach((planet, i) => {
    setTimeout(async () => {
      try {
        const reply = await postChronos(`exoplanet:${planet}`);
        upsertPlanetCard(planet, reply);
      } catch (err) {
        console.warn("Seed planet failed", planet, err);
      }
    }, 800 + i * 700);
  });
})();
