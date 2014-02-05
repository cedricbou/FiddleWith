r = http.url("http://localhost:8089/greets").get
if r.is2XX then
  r.body
else
  response.auto r
end

