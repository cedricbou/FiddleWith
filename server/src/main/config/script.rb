class DynaBeanWrapper
	def initialize(obj)
		@obj = obj
	end
	def method_missing(method_name, *args)
		DynaBeanWrapper(@obj.send("get", method_name.to_s)).new
	end
	def to_s
		@obj.to_s
	end
end

data = DynaBeanWrapper.new(json)

5.times {
	puts data.person.name
}
