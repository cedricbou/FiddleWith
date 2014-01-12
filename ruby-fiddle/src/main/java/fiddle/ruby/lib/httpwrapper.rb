class HttpSugar
  
  def initialize(rsc)
    @registry = rsc.https
    @http = rsc.default_http
  end
  
  def get(url)
    @http.with_url(url).get
  end
  
  def getWithOptions(url, options)
    configured = @http.with_url url
    
    if options[:headers] then
      configured = configured.with_headers options[:headers]
    end
    
    configured.get
  end
  
  def post(url, data)
    @http.with_url(url).post(data)
  end

  def postWithOptions(url, data, options)
    configured = @http.with_url url
    
    if options[:headers] then
      configured = configured.with_headers options[:headers]
    end
    
    configured.post(data)
  end
    
  def method_missing(method_name, *args)
    @registry.get_http(method_name)
  end
end