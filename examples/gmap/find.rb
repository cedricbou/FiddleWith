coord = sql.geo.single("select latitude, longitude, record from geo where city = ? and zip = ?", data.city, data.zip);

if coord.exists? and dates.mysql(coord.record).youngerThan(7, dates.DAYS) then
	response.ok({'lat' => coord.latitude, 'lng' =>  coord.longitude})
else
	gres = http.geo.get({'city' => data.city, 'zip' => data.zip})
	
	if gres.success? and gres.xml.result.geometry.location.exists? then
		sql.geo.exec("insert into geo (city, zip, latitude, longitude, record) values (?, ?, ?, ?, NOW())");
		response.ok({'lat' => gres.xml.result.geometry.location.lat.to_s, 'lng' => gres.xml.result.geometry.location.lng.to_s})
	else
		response.ko("service unavailable")
	end
end
