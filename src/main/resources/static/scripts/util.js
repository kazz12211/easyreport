function getTzOffset() {
	var date = new Date();
	return (date.getHours() - date.getUTCHours() + 24) % 24;
}
	
function getbutton(label, name, value) {
	return {
		item: 'Button',
		type: 'ts-secondary ts-micro',
		label: label,
		name: name,
		value: value
	};
}
