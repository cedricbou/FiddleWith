include Java
java_import Java::fiddle.httpclient.FiddleHttpResponse

JFiddleHttpResponse = FiddleHttpResponse

class JFiddleHttpResponse
  alias old_json json
  alias old_xml xml
  
  def json
    JsonSugar.new(old_json)
  end
  
  def xml
    XmlSugar.new(old_xml)
  end
end

class ConfiguredHttpSugar
  def initialize(http)
    @http = http
    yield self if block_given?
  end
  
  # DSL method
  def header(key, value)
    @http = @http.with_header(key, value)
  end
  
  def headers(headers = {})
    @http = @http.with_headers(headers)
  end
  
  # Http Methods
  def get(templateVars = {})
    if templateVars.length > 0 then
      @http.get templateVars
    else
      @http.get
    end
  end
  
  def post(data = nil, templateVars = {})
    if data then
      if templateVars.length > 0 then
        @http.post(data, templateVars)
      else
        @http.post(data)
      end
    else
      if templateVars.length > 0 then
        @http.post(templateVars)
      else
        @http.post
      end
    end
  end
end

class HttpSugar
  
  def initialize(rsc)
    @rsc = rsc
    @http = rsc.http
  end
    
  def url(url)
    ConfiguredHttpSugar.new @http.with_url url 
  end
    
  def method_missing(method_name, *args)
    ConfiguredHttpSugar.new @rsc.http(method_name.to_s)
  end
end
