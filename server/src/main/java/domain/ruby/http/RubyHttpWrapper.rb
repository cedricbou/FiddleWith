
class HttpSugar
	def initialize(http)
		@http = http
	end
	
	def method_missing(method_name, *args)
    if method_name.to_s == "post" || method_name.to_s == "get" then
      HttpResponseSugar.new(@http.send(method_name, *args))
    else
      ConfiguredHttpSugar.new(@http.http(method_name.to_s))
   	end
	end
end

class ConfiguredHttpSugar
  def initialize(http)
    @http = http
  end
  
  def method_missing(method_name, *args)
    if method_name.to_s == "post" || method_name.to_s == "get" then
      HttpResponseSugar.new(@http.send(method_name, *args))
    else 
      @http.send(method_name, *args)
    end
  end
end

class HttpResponseSugar
  def initialize(response)
    @r = response
  end
  
  def success?
    @r.is_ok
  end
  
  def redirected?
    @r.is_redirect
  end
  
  def error?
    @r.is_error
  end
  
  def text
    @r.as_text
  end
  
  def xml
    XmlSugar.new(@r.as_xml)
  end
  
  def json
    JsonSugar.new(@r.as_json)
  end
  
  def to_s
    @r.as_text
  end
end