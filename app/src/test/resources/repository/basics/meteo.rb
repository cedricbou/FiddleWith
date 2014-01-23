temp = http.meteo.get({ 'city' => d.city, 'format' => 'json'} ).json.city.temp
wind = http.meteo.get({ 'city' => d.city, 'format' => 'xml'} ).xml.city.wind
"meteo : %s : %d, %d" % [d.city, temp, wind.value]
