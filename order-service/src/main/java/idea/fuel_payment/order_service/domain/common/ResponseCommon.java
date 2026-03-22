package idea.fuel_payment.order_service.domain.common;

public interface ResponseCommon<R, M> {
	R error(String Message);

	R success(M m);
}
