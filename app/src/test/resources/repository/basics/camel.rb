
def camelRoute(camel)
	camel.from("direct:a").to("fiddle:basics:camelReceiver")
end