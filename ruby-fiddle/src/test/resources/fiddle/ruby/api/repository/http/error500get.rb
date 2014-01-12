r = http.get("http://localhost:8089/greets")
if r.is2XX then
  r.body
else
  response.auto r
end

