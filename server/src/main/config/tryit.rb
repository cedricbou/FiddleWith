class Person
	def initialize(name)
		@name = name
		@pets = [Pet.new('dog'), Pet.new('cat'), 'birds', true, 5.45, 3, [4..7], {'t' => 4.5, 'RR' => 'toto'}]
	end
	
	def to_s
		@name
	end
end

class Pet
	def initialize(kind)
		@kind = kind
	end
end

h = sql.local.singleLine("select 'toto' as t1, 'titi' as t2");
response.ok(data.to_hash)

#db.local.withHandle do |h|
#  h.createQuery("select 'toto' as name").each do |rs|
#    puts rs['name']
#  end
#end

# p = Person.new("toto")

# response.ok(p)
