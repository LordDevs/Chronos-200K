(() => {
  const apiUrl = `${window.location.origin}/chat/api`;
  const messagesEl = document.getElementById("chat-messages");
  const form = document.getElementById("chatForm");
  const input = document.getElementById("chatInput");
  const linkStatus = document.getElementById("linkStatus");
  const planetCards = document.getElementById("planetCards");
  const quickReplies = document.getElementById("quickReplies");

  const seedPlanets = ["kepler-442 b", "proxima centauri b", "k2-18 b"];

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
    const response = await fetch(apiUrl, {
      method: "POST",
      headers: { "Content-Type": "text/plain;charset=UTF-8" },
      body: message,
    });
    if (!response.ok) {
      throw new Error("HTTP " + response.status);
    }
    return response.text();
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
      appendMessage("Link perdido com CHRONOS. Confirme que o backend está em :8080.", "bot");
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

  appendMessage(
    "Sistema CHRONOS inicializado. Canal tático pronto. Use os atalhos ou digite um comando.",
    "bot"
  );

  // Prefetch telemetry cards without flooding the chat log
  seedPlanets.forEach((planet, i) => {
    setTimeout(async () => {
      try {
        const reply = await postChronos(`exoplanet:${planet}`);
        linkStatus.classList.add("online");
        upsertPlanetCard(planet, reply);
      } catch (err) {
        linkStatus.classList.remove("online");
        console.warn("Seed planet failed", planet, err);
      }
    }, 300 + i * 500);
  });
})();
