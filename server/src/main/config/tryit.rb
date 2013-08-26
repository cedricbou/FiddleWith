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

1.times {
	puts data.person.name.last.to_s + " " + data.person.name.first.to_s
}

#db.local.withHandle do |h|
#  h.createQuery("select 'toto' as name").each do |rs|
#    puts rs['name']
#  end
#end

p = Person.new("toto")

response.ok(p)
