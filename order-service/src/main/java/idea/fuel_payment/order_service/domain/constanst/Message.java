package idea.fuel_payment.order_service.domain.constanst;

public enum Message {
	// Vehicle messages
	NO_EXIST_VEHICLE_MODEL("No exists vehicle model: %s"),
	INVALID_LENGTH_NAME("Name's %s should shorter than 100 characters");

	private final String template;

	Message(String template) {
		this.template = template;
	}

	public String format(Object... args) {
		if (args == null || args.length == 0) {
			return this.template;
		}
		return String.format(this.template, args);
	}
}
