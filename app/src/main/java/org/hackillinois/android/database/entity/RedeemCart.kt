data class RedeemCart(
    val items: List<CartItem>
)

data class CartItem(
    val itemId: String,
    val name: String,
    val quantity: Int
)