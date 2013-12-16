class DbiSugar
  def initialize(rsc)
    @registry = rsc.dbis;
  end
  
  def method_missing(method_name, *args)
    @registry.get_decorated_dbi(method_name)
  end
end
