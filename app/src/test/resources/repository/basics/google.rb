http.url('http://www.google.fr').trace { |r| puts(">>>> " + r.to_s) }.post('q=search').to_s
