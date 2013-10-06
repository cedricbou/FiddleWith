class XmlSugar
  def initialize(xml)
    @xml = xml
  end
  
  def get(name)
    XmlSugar.new(@xml.get(name))
  end
  
  def value
    @xml.value
  end
  
  def list
    @xml.list.map { |e| XmlSugar.new(e) }
  end
  
  def method_missing(method_name, *args, &block)
    if method_name.to_s =~ /^to_\w+/ then
      to_s.send(method_name, *args, &block)
    else
      XmlSugar.new(@xml.send("get", method_name, &block))
    end
  end
    
  def to_s
    @xml.to_s
  end
end

class JsonSugar
  def initialize(obj)
    @obj = obj
  end
  
  def method_missing(method_name, *args, &block)
    if method_name.to_s =~ /^to_\w+/ then
      to_s.send(method_name, *args, &block)
    else
      JsonSugar.new(@obj.send("get", method_name, &block))
    end
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