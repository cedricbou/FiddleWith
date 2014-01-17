class DbiSugar
  def initialize(rsc)
    @rsc = rsc;
  end
  
  def method_missing(method_name, *args)
    @rsc.dbi(method_name)
  end
end
