include Java
java_import Java::fiddle.httpclient.FiddleHttpResponse

JFiddleHttpResponse = FiddleHttpResponse

class JFiddleHttpResponse
  alias old_json json
  def json
    JsonSugar.new(old_json)
  end
end

class HttpSugar
  
  def initialize(rsc)
    @rsc = rsc
    @http = rsc.http
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
    @rsc.http(method_name.to_s)
  end
end
