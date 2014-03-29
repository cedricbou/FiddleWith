
def camelRoute(camel)
	camel.from("direct:a").to("fiddle:basics:camelReceiver")
	camel.from("quartz2://grp/aaa?trigger.repeatCount=3").to("fiddle:basics:camelReceiver")
end