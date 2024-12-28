package sk.sfabian.myeliquid.repository.model

data class Movement(
    val quantity: Double,
    val totalPrice: Double,
    val type: String,
    val timestamp: Long = System.currentTimeMillis()
)