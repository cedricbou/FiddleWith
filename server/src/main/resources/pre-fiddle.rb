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


class SqlSugar
	def initialize(sqlRegistry)
		@registry = sqlRegistry
	end
	
	def method_missing(method_name, *args)
		@registry.get(method_name)
	end
end

sql = SqlSugar.new(sql)
data = DynaBeanWrapper.new(json)

