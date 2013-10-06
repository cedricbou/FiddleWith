require 'domain/ruby/data/DataWrapper.rb'
require 'domain/ruby/http/RubyHttpWrapper.rb'

class SqlSugar
	def initialize(sqlRegistry)
		@registry = sqlRegistry
	end
	
	def method_missing(method_name, *args)
		@registry.get(method_name)
	end
end

sql = SqlSugar.new(_sql)
http = HttpSugar.new(_http)
data = JsonSugar.new(_json)

