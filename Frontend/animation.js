
document.addEventListener('DOMContentLoaded', initWeatherEffects);


//function to create effect on screen
function createRain() {
    const rainContainer = document.querySelector('.weather-effects');
    rainContainer.innerHTML = '';
  
      for(let i = 0; i < 50; i++) {
          const drop = document.createElement('div');
          drop.className = 'rain-drop';
          drop.style.left = Math.random() * 100 + 'vw';
          drop.style.animationDelay = Math.random() * 2 + 's';
          rainContainer.appendChild(drop);
      }
  }
  
  function updateWeatherEffects(message) {
    if (message.toLowerCase().includes('rain')) {
        changeWeather('rain');
    } else if (message.toLowerCase().includes('cloud')) {
        changeWeather('cloudy');
    }
  }
  
  function initWeatherEffects(){
    const effectsContainer = document.createElement('div');
    effectsContainer.className = 'weather-effects';
    document.body.appendChild(effectsContainer);
  }
  
  function updateWeatherBackground(condition) {
    const body = document.body;
    body.classList.remove('rainy-theme', 'snow-theme', 'sunny-theme');

    if (condition === 'rain') {
        body.classList.add('rainy-theme');
        createRain();
    } else if (condition === 'snow') {
        body.classList.add('snow-theme');
        createSnow();
    } else if (condition === 'sunny') {
        body.classList.add('sunny-theme');
        createSunny();
    }
}
  
  
  //snow activation

  function createSnow() {
    const snowContainer = document.querySelector('.weather-effects');
    snowContainer.innerHTML = '';

    for (let i = 0; i < 50; i++) {
        const flake = document.createElement('div');
        flake.className = 'snow-flake';
        flake.style.left = Math.random() * 100 + 'vw';
        flake.style.animationDelay = Math.random() * 5 + 's';
        snowContainer.appendChild(flake);
    }
}

function changeWeather(condition) {
    console.log("Weather condition:", condition); // Verifica se está sendo chamado
    initWeatherEffects(); // Garante que a div existe

    const effects = document.querySelector('.weather-effects');
    effects.innerHTML = '';
    
    if (condition === 'rain') {
        createRain();
    } else if (condition === 'cloudy') {
        effects.innerHTML = `
            <div class="cloud" style="top:30%">☁️</div>
            <div class="cloud" style="top:50%">⛅</div>
            <div class="fog-layer"></div>
        `;
    }
}

document.addEventListener('DOMContentLoaded', () => {
    initWeatherEffects();
    setTimeout(() => {
        changeWeather('rain');
    }, 500); // Aguarda 500ms antes de ativar a chuva
});

import { motion } from "framer-motion";

export default function ProgressBar({ progress }) {
  return (
    <div className="relative w-full h-6 bg-gray-800 rounded-2xl border border-blue-500 shadow-lg overflow-hidden">
      <motion.div
        className="h-full bg-gradient-to-r from-blue-500 to-purple-500"
        initial={{ width: 0 }}
        animate={{ width: `${progress}%` }}
        transition={{ duration: 0.5, ease: "easeOut" }}
      />
      <span className="absolute inset-0 flex items-center justify-center text-white font-semibold text-sm">
        {progress}%
      </span>
    </div>
  );
}