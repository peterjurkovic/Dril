package sk.peterjurkovic.dril.listener;


public interface AsyncLIstener {
	void onCheckResponse(Integer response);
	void onUpdatedResponse(Integer response);
}
