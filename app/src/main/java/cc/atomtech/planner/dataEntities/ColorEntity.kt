package cc.atomtech.planner.dataEntities

data class ColorEntity(
   var red: Int = 0,
   var green: Int = 0,
   var blue: Int = 0
) {
   fun buildByHex(hex: String) {
      this.red = hex.substring(0, 2).toInt(radix = 16)
      this.green = hex.substring(2, 4).toInt(radix = 16)
      this.blue = hex.substring(4, 6).toInt(radix = 16)
   }

   fun getHexString(): String {
      return "fafafa"
   }
}
