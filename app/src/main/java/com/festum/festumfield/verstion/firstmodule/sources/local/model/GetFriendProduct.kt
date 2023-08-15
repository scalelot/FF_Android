package com.festum.festumfield.verstion.firstmodule.sources.local.model

import java.io.Serializable

data class GetFriendProduct(
	val friendid: String? = null,
	val page: Int? = null,
	val search: String? = null,
	val limit: Int? = null,
	val sortfield: String? = null,
	val sortoption: Int? = null
) : Serializable

