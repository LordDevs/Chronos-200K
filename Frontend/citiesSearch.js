let cities = [];
// Load cities from cities.txt
function loadCities() {
    $.ajax({
        url: 'cities.txt',
        method: 'GET',
        dataType: 'text',
        success: function(data) {
            cities = data.split('\n')
                .map(city => city.trim())
                .filter(city => city.length > 0);
            $('#cityInput').prop('disabled', false);
            console.log(`Loaded ${cities.length} cities`);
        },
        error: function(xhr, status, error) {
            console.error('Error loading cities:', error);
            $('#cityInput').attr('placeholder', 'Error loading cities');
        }
    });
}

// Debounce function
function debounce(func, wait) {
    let timeout;
    return function(...args) {
        clearTimeout(timeout);
        timeout = setTimeout(() => func.apply(this, args), wait);
    };
}

// Optimized search function with limit
function searchCities(query, maxResults = 5) {
    const q = query.toLowerCase();
    const results = [];
    let count = 0;

    // Binary search to find starting point
    let startIndex = binarySearchStart(q);
    
    // Collect matches
    for (let i = startIndex; i < cities.length && count < maxResults; i++) {
        const cityLower = cities[i].toLowerCase();
        if (cityLower.startsWith(q)) {
            results.push(cities[i]);
            count++;
        } else if (cityLower > q) {
            break;
        }
    }
    displaySuggestions(results);
}

// Binary search for starting point
function binarySearchStart(query) {
    let left = 0;
    let right = cities.length - 1;
    
    while (left <= right) {
        const mid = Math.floor((left + right) / 2);
        const cityLower = cities[mid].toLowerCase();
        
        if (cityLower < query) {
            left = mid + 1;
        } else {
            right = mid - 1;
        }
    }
    return left;
}

// Display suggestions using jQuery
function displaySuggestions(filteredCities) {
    const $suggestions = $('#suggestions');
    $suggestions.empty();
    
    if (filteredCities.length > 0 && $('#cityInput').val().trim() !== '') {
        $.each(filteredCities, function(index, city) {
            $('<div>')
                .addClass('suggestion-item')
                .text(city)
                .on('click', function() {
                    $('#cityInput').val(city);
                    $suggestions.hide();
                })
                .appendTo($suggestions);
        });
        $suggestions.show();
    } else {
        $suggestions.hide();
    }
}

// Event handlers with jQuery
$(document).ready(function() {
    const debouncedSearch = debounce(searchCities, 200);

    $('#cityInput').on('input', function() {
        debouncedSearch($(this).val());
    });

    $(document).on('click', function(e) {
        if (!$(e.target).closest('#cityInput, #suggestions').length) {
            $('#suggestions').hide();
        }
    });

    $('#cityInput').on('focus', function() {
        if ($(this).val().trim() !== '') {
            searchCities($(this).val());
        }
    });

    // Start loading cities
    loadCities();
});