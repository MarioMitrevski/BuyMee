package com.buymee.network

import android.os.Parcel
import android.os.Parcelable
import java.net.URL
import java.util.*

data class ShopList(
    val page: Int,
    val totalPages: Int,
    val pageSize: Int,
    val totalElements: Int,
    val content: List<Shop>
)

data class Shop(
    val shopId: UUID? = null,
    val shopName: String? = null,
    val shopDescription: String? = null,
    val shopCategory: Long? = null,
    val createdDate: String? = null,
    val shopLogo: URL? = null
)

data class ShopDetailsDTO(
    val shopId: String,
    val shopName: String,
    val shopDescription: String,
    val categoryId: Long,
    val createdDate: String,
    val shopLogoImage: String,
    val products: FeaturedProducts
)

data class FeaturedProducts(
    val page: Int,
    val totalPages: Int,
    val pageSize: Int,
    val totalElements: Int,
    val content: List<FeaturedProduct>
)

data class FeaturedProduct(
    val productId: String,
    val productName: String,
    val productDescription: String,
    val price: Double,
    val imageURL: String,
    val shopId: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(productId)
        parcel.writeString(productName)
        parcel.writeString(productDescription)
        parcel.writeDouble(price)
        parcel.writeString(imageURL)
        parcel.writeString(shopId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FeaturedProduct> {
        override fun createFromParcel(parcel: Parcel): FeaturedProduct {
            return FeaturedProduct(parcel)
        }

        override fun newArray(size: Int): Array<FeaturedProduct?> {
            return arrayOfNulls(size)
        }
    }
}

data class ShopProductsResponse(
    val page: Int,
    val totalPages: Int,
    val pageSize: Int,
    val totalElements: Int,
    val content: List<ShopProduct>
)

data class ShopProduct(
    val productId: String,
    val productName: String,
    val productDescription: String,
    val price: Double,
    val imageURL: String,
    val shopId: String
)

data class ProductDetailsDTO(
    val productId: String,
    val productName: String,
    val productDescription: String,
    val price: Double,
    val imagesUrls: List<String>,
    var productItems: List<ProductItem>,
    val productReviews: List<ProductReviewDTO>
)

data class ProductItem(
    val productItemId: String,
    val quantityInStock: Int,
    val product: String,
    val price: Double,
    val attributes: Set<Attribute>
)

data class ProductReviewDTO(
    val firstName: String,
    val lastName: String,
    val comment: String,
    val grade: Int,
    val createdDate: String
)

data class Attribute(
    val attributeName: String,
    val attributeValue: String
)

data class Category(
    val categoryId: Long,
    val superCategoryId: Long?,
    val categoryName: String
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class AddToCartRequest(
    val productItemId: String,
    val cartItemQuantity: String
)

data class CartResponse(
    val cartId: String,
    val cartItems: List<CartItem>,
    val subTotal: Double,
    val discount: Double,
    val total: Double
)

data class CartItem(
    val cartItemId: String,
    val productName: String,
    val imageUrl: String,
    val cartItemQuantity: Int
)