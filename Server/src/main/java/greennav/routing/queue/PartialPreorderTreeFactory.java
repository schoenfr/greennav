package greennav.routing.queue;

public class PartialPreorderTreeFactory<K, V> implements
		IPartialPreorderQueueFactory<K, V, PartialPreorderTree<K, V>> {

	@Override
	public String getIdentifier() {
		return "pptree";
	}

	@Override
	public PartialPreorderTree<K, V> createEmpty(IPartialPreorder<V> preorder) {
		return new PartialPreorderTree<K, V>(preorder);
	}

	@Override
	public PartialPreorderTree<K, V> createEmpty(ITotalPreorder<V> preorder) {
		return new PartialPreorderTree<K, V>(preorder);
	}
}
