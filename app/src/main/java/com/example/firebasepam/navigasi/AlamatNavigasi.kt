package com.example.firebasepam.navigasi

interface AlamatNavigasi{
    val route: String
    val titleRes: String
}

object DestinasiHome : AlamatNavigasi{
    override val route: String = "home"
    override val titleRes: String = "home"
}

object  DestinasiInsert : AlamatNavigasi{
    override val titleRes: String = "home"
    override val route: String = "home"

}

object DestinasiDetail : AlamatNavigasi{
    override val route: String = "detail"
    override val titleRes: String = "detail"
    const val nim = "nim"
    val routesWithArgs = "$route/{$nim}"
}

object DestinasiUpdate : AlamatNavigasi{
    override val route: String = "update"
    override val titleRes: String = "update"
    const val nim = "nim"
    val routesWithArgs = "$route/{$nim}"
}