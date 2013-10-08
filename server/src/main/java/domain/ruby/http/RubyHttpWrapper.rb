module J
	include Java
	
	Optional = com.google.common.base.Optional
  URI = java.net.URI
  
  include_class "domain.TemplateId"
end

class HttpSugar
	def initialize(registry, http)
		@registry = registry
		@http = http
	end
	
	def http(key)
		configured = @registry.configured key
		raise "no http configuration found for #{key}" if configured.nil?
		ConfiguredHttpSugar.new(configured)
	end
	
	def _options(options)
		options = {
			:queryString => J::Optional::absent,
			:template => J::Optional::absent,
			:entity => J::Optional::absent
		}.merge(options){|key, oldval, newval| if key == :template then J::Optional::fromNullable J::TemplateId.new(newval) else J::Optional::fromNullable newval end }
	end
	
	def get(url, options = {})
		options = _options(options)
		
		HttpResponseSugar.new(
			@http.get(J::URI.new(url), options[:queryString], @registry.workspace_id, options[:template], options[:entity]))
	end
	
	def post(url, options = {})
		options = _options(options)
		
		HttpResponseSugar.new(
			@http.post(J::URI.new(url), options[:queryString], @registry.workspace_id, options[:template], options[:entity]))
	end
	
	def method_missing(method_name, *args)
		http(method_name.to_s)
	end
end

class ConfiguredHttpSugar
  def initialize(http)
    @http = http
  end
 
  def _options(options)
    options = {
      :queryString => J::Optional::absent,
      :entity => J::Optional::absent
    }.merge(options){|key, oldval, newval| J::Optional::fromNullable newval}
  end

  def post(options = {})
    options = _options(options)
    
    puts options[:entity]
    
    HttpResponseSugar.new(
      @http.post(options[:queryString], options[:entity]))
  end

  def get(options = {})
    options = _options(options)
    
    HttpResponseSugar.new(
      @http.get(options[:queryString], options[:entity]))
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
