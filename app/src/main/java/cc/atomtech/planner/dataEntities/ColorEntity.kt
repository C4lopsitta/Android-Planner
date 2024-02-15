package cc.atomtech.planner.dataEntities

data class ColorEntity(
   var red: Int = 0,
   var green: Int = 0,
   var blue: Int = 0
) {
   fun buildByHex(hex: String) {
      this.red = hex.subSequence(0, 1).toString().toInt(radix = 16)
      this.green = hex.subSequence(2, 3).toString().toInt(radix = 16)
      this.blue = hex.subSequence(4, 5).toString().toInt(radix = 16)
   }

   fun getHexString(): String {
      return "fafafa"
   }
}
