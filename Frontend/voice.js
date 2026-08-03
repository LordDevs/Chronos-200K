/**
 * CHRONOS voice channel — Web Speech API (browser-only).
 * Locale: pt-PT (default) | en-US
 */

const VOICE_LOCALES = {
  "pt-PT": {
    code: "pt-PT",
    label: "PT",
    voicePrefix: "pt",
    statusListen: "VOZ // a ouvir…",
    statusStandby: "VOZ // standby",
    statusError: "VOZ // erro",
  },
  "en-US": {
    code: "en-US",
    label: "EN",
    voicePrefix: "en",
    statusListen: "VOICE // listening…",
    statusStandby: "VOICE // standby",
    statusError: "VOICE // error",
  },
};

const STORAGE_KEY = "chronos-voice-lang";

/** Normalize STT quirks before AIML routing (PT + EN). */
window.normalizeVoiceTranscript = function normalizeVoiceTranscript(raw) {
  let text = (raw || "").trim().replace(/\s+/g, " ");
  if (!text) return text;

  const key = text
    .toLowerCase()
    .normalize("NFD")
    .replace(/\p{M}/gu, "")
    .replace(/[.,!?;:]/g, "")
    .trim();

  const exact = {
    // PT
    "ola": "OLA",
    "ola chronos": "OLA CHRONOS",
    "bom dia": "BOM DIA",
    "status da nave": "STATUS DA NAVE",
    "estado da nave": "ESTADO DA NAVE",
    "telemetria": "TELEMETRIA",
    "verificar oxigenio": "VERIFICAR OXIGENIO",
    "niveis de oxigenio": "NIVEIS DE OXIGENIO",
    "analisar planeta oceano": "ANALISAR PLANETA OCEANO",
    "analisar o planeta oceano": "ANALISAR PLANETA OCEANO",
    "analisar planeta selva": "ANALISAR PLANETA SELVA",
    "analisar o planeta selva": "ANALISAR PLANETA SELVA",
    "ativar protocolo apex": "ATIVAR PROTOCOLO APEX",
    "ativar o protocolo apex": "ATIVAR PROTOCOLO APEX",
    "protocolo apex": "PROTOCOLO APEX",
    "protocolo apex chronos": "PROTOCOLO APEX CHRONOS",
    "ativar cap": "ATIVAR CAP",
    "prever evolucao 200 mil": "PREVER EVOLUCAO 200 MIL",
    "prever evolucao 200k": "PREVER EVOLUCAO 200K",
    "evolucao 200 mil anos": "EVOLUCAO 200 MIL ANOS",
    "ajuda": "AJUDA",
    "quem es tu": "QUEM ES TU",
    // EN
    "hello": "HI",
    "hi chronos": "OLA CHRONOS",
    "good morning": "GOOD MORNING",
    "ship status": "SHIP STATUS",
    "check oxygen": "CHECK OXYGEN",
    "check oxygen levels": "CHECK OXYGEN LEVELS",
    "oxygen levels": "OXYGEN LEVELS",
    "analyze ocean planet": "ANALYZE OCEAN PLANET",
    "analyse ocean planet": "ANALYZE OCEAN PLANET",
    "analyze jungle planet": "ANALYZE JUNGLE PLANET",
    "analyse jungle planet": "ANALYZE JUNGLE PLANET",
    "activate apex protocol": "ACTIVATE APEX PROTOCOL",
    "apex protocol": "APEX PROTOCOL",
    "activate cap": "ACTIVATE CAP",
    "predict evolution 200k": "PREDICT EVOLUTION 200K",
    "predict evolution 200 k": "PREDICT EVOLUTION 200K",
    "predict evolution two hundred thousand": "PREDICT EVOLUTION 200K",
    "who are you": "WHO ARE YOU",
    "help": "HELP",
    "system protocols": "SYSTEM PROTOCOLS",
    "list colonies": "LIST COLONIES",
    "colony archive": "COLONY ARCHIVE",
    "deep scan": "DEEP SCAN PLANET kepler-442 b",
    "deep scan planet": "DEEP SCAN PLANET kepler-442 b",
    "scan profundo": "DEEP SCAN PLANET kepler-442 b",
    "scan profundo kepler 442 b": "DEEP SCAN PLANET kepler-442 b",
    "comparar planetas": "COMPARE PLANETS kepler-442 b AND proxima centauri b",
    "compare planets": "COMPARE PLANETS kepler-442 b AND proxima centauri b",
    "arquivar no vault": "VAULT ARCHIVE kepler-442 b",
    "vault archive": "VAULT ARCHIVE kepler-442 b",
    "listar vault": "VAULT LIST",
    "vault list": "VAULT LIST",
  };

  if (exact[key]) return exact[key];

  text = text.replace(/\b200\s*mil\b/gi, "200K");
  text = text.replace(/\bduzentos\s*mil\b/gi, "200K");
  text = text.replace(/\btwo\s*hundred\s*thousand\b/gi, "200K");
  text = text.replace(/\b200\s*k\b/gi, "200K");

  // STT often drops hyphens in exoplanet names: "kepler 442 b" → "kepler-442 b"
  text = text.replace(/\bkepler\s+(\d+)\s+([a-z])\b/gi, "kepler-$1 $2");
  text = text.replace(/\bproxima\s+centauri\s+([a-z])\b/gi, "proxima centauri $1");
  text = text.replace(/\bk2\s+18\s+([a-z])\b/gi, "k2-18 $1");

  return text;
};

window.createVoiceChannel = function createVoiceChannel({ onTranscript, onStatus, onError }) {
  const SpeechRecognition =
    window.SpeechRecognition || window.webkitSpeechRecognition;

  const supported = Boolean(SpeechRecognition) && "speechSynthesis" in window;

  let recognition = null;
  let listening = false;
  let ttsEnabled = true;
  let lang =
    (typeof localStorage !== "undefined" && localStorage.getItem(STORAGE_KEY)) || "pt-PT";
  if (!VOICE_LOCALES[lang]) lang = "pt-PT";

  function locale() {
    return VOICE_LOCALES[lang];
  }

  function setStatus(msg) {
    if (onStatus) onStatus(msg);
  }

  function stripHtml(html) {
    const el = document.createElement("div");
    el.innerHTML = html;
    return (el.textContent || "").replace(/\s+/g, " ").trim();
  }

  function pickVoice(utterLang) {
    const voices = window.speechSynthesis.getVoices();
    const prefix = locale().voicePrefix;
    return (
      voices.find((v) => v.lang === utterLang) ||
      voices.find((v) => v.lang.toLowerCase().startsWith(prefix)) ||
      null
    );
  }

  function speak(text) {
    if (!ttsEnabled || !text) return;
    window.speechSynthesis.cancel();
    const utter = new SpeechSynthesisUtterance(text.slice(0, 600));
    utter.lang = lang;
    utter.rate = 0.95;
    utter.pitch = 0.9;
    const match = pickVoice(lang);
    if (match) utter.voice = match;
    window.speechSynthesis.speak(utter);
  }

  function bindRecognitionHandlers(rec) {
    rec.onstart = () => {
      listening = true;
      setStatus(locale().statusListen);
    };

    rec.onend = () => {
      listening = false;
      setStatus(locale().statusStandby);
    };

    rec.onerror = (ev) => {
      listening = false;
      const msg =
        ev.error === "not-allowed"
          ? lang.startsWith("pt")
            ? "Microfone bloqueado. Permita o acesso no browser."
            : "Microphone blocked. Allow access in the browser."
          : lang.startsWith("pt")
            ? `Erro de voz: ${ev.error}`
            : `Voice error: ${ev.error}`;
      setStatus(locale().statusError);
      if (onError) onError(msg);
    };

    rec.onresult = (ev) => {
      const transcript = ev.results[0][0].transcript.trim();
      if (transcript && onTranscript) {
        onTranscript(window.normalizeVoiceTranscript(transcript));
      }
    };
  }

  function ensureRecognition() {
    if (!SpeechRecognition) return null;
    if (recognition) return recognition;

    recognition = new SpeechRecognition();
    recognition.lang = lang;
    recognition.interimResults = false;
    recognition.continuous = false;
    recognition.maxAlternatives = 1;
    bindRecognitionHandlers(recognition);
    return recognition;
  }

  function rebuildRecognition() {
    const wasListening = listening;
    if (recognition) {
      try {
        if (listening) recognition.stop();
      } catch (_) {
        /* ignore */
      }
      recognition = null;
    }
    listening = false;
    ensureRecognition();
    return wasListening;
  }

  return {
    isSupported: () => supported,

    isListening: () => listening,

    isTtsEnabled: () => ttsEnabled,

    getLang: () => lang,

    getLangLabel: () => locale().label,

    /** @param {"pt-PT"|"en-US"} next */
    setLang(next) {
      if (!VOICE_LOCALES[next] || next === lang) return lang;
      lang = next;
      try {
        localStorage.setItem(STORAGE_KEY, lang);
      } catch (_) {
        /* ignore */
      }
      rebuildRecognition();
      setStatus(locale().statusStandby + ` · ${locale().label}`);
      return lang;
    },

    toggleLang() {
      return this.setLang(lang === "pt-PT" ? "en-US" : "pt-PT");
    },

    toggleTts() {
      ttsEnabled = !ttsEnabled;
      if (!ttsEnabled) window.speechSynthesis.cancel();
      return ttsEnabled;
    },

    toggleListen() {
      const rec = ensureRecognition();
      if (!rec) {
        if (onError) {
          onError(
            lang.startsWith("pt")
              ? "Reconhecimento de voz não suportado neste browser. Use Chrome ou Edge."
              : "Speech recognition not supported. Use Chrome or Edge."
          );
        }
        return false;
      }
      if (listening) {
        rec.stop();
        return false;
      }
      try {
        rec.lang = lang;
        rec.start();
        return true;
      } catch (e) {
        if (onError) {
          onError(
            lang.startsWith("pt")
              ? "Não foi possível iniciar o microfone."
              : "Could not start the microphone."
          );
        }
        return false;
      }
    },

    stopListen() {
      if (recognition && listening) recognition.stop();
    },

    speakPlain(htmlOrText) {
      speak(stripHtml(htmlOrText));
    },

    stopSpeak() {
      window.speechSynthesis.cancel();
    },
  };
};
