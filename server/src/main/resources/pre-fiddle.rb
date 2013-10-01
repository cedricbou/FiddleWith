class DynaBeanWrapper
	def initialize(obj)
		@obj = obj
	end
	
	def method_missing(method_name, *args)
		DynaBeanWrapper.new(@obj.send("get", method_name.to_s))
	end
	
	def to_hash
		if @obj.value_node?
			if @obj.integral_number?
				@obj.as_long
			elsif @obj.floating_point_number?
				@obj.as_double
			elsif @obj.boolean?
				@obj.as_boolean
			else
				@obj.text_value
			end
		elsif @obj.array?
			a = []
			@obj.iterator.each { |node| a.push DynaBeanWrapper.new(node).to_hash }
			a
		else
			h = {}
			@obj.field_names.each { |field| h[field] = DynaBeanWrapper.new(@obj.get(field)).to_hash }
			h
		end
	end
	
	def to_s
		@obj.asText
	end
end


class SqlSugar
	def initialize(sqlRegistry)
		@registry = sqlRegistry
	end
	
	def method_missing(method_name, *args)
		@registry.get(method_name)
	end
end

class HttpSugar
	def initialize(http)
		@http = http
	end
	
	def method_missing(method_name, *args)
		if method_name.to_s == "post" || method_name.to_s == "get" then
			@http.send(method_name, *args)
		else
			@http.http(method_name.to_s)
		end
	end
end

sql = SqlSugar.new(sql)
http = HttpSugar.new(http)
data = DynaBeanWrapper.new(json)

