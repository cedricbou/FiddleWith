class DynaBeanWrapper
	def initialize(obj)
		@obj = obj
	end
	
	def method_missing(method_name, *args)
		DynaBeanWrapper.new(@obj.send("get", method_name.to_s))
	end
	
	def to_s
		@obj.asText
	end
end

data = DynaBeanWrapper.new(json)

