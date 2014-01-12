r = http.post("http://localhost:8089/greets", "john")
if r.is2XX then
  r.body
else
  response.auto r
end

