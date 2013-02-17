package dao.impl.orm.squeryl.common.tables

import java.util.Date

case class Products(
                     productId: Long,
                     title:String,
                     description:String,
                     price:Double,
                     addedDate:Date,
                     brandId:Int,
                     shortDescription:String,
                     defaultImageId:Int,
                     visitCounter:Int,
                     createdByUser:Int,
                     archived:Int)
