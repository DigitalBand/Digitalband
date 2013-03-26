package webServices

import common.ImageSearch
import models.{ImageSearchItem, ListPage}
import concurrent.Future
import play.api.libs.json.{JsArray, Json}
import play.api.libs.concurrent.Execution.Implicits._

class FakeImageSearch extends ImageSearch {
  def getList[S](search: String, pageNumber: Int)(f: (ListPage[ImageSearchItem]) => S): Future[S] = {
    val json =  Json.parse("""
    {
 "kind": "customsearch#search",
 "url": {
  "type": "application/json",
  "template": "https://www.googleapis.com/customsearch/v1?q={searchTerms}&num={count?}&start={startIndex?}&lr={language?}&safe={safe?}&cx={cx?}&cref={cref?}&sort={sort?}&filter={filter?}&gl={gl?}&cr={cr?}&googlehost={googleHost?}&c2coff={disableCnTwTranslation?}&hq={hq?}&hl={hl?}&siteSearch={siteSearch?}&siteSearchFilter={siteSearchFilter?}&exactTerms={exactTerms?}&excludeTerms={excludeTerms?}&linkSite={linkSite?}&orTerms={orTerms?}&relatedSite={relatedSite?}&dateRestrict={dateRestrict?}&lowRange={lowRange?}&highRange={highRange?}&searchType={searchType}&fileType={fileType?}&rights={rights?}&imgSize={imgSize?}&imgType={imgType?}&imgColorType={imgColorType?}&imgDominantColor={imgDominantColor?}&alt=json"
 },
 "queries": {
  "nextPage": [
   {
    "title": "Google Custom Search - FENDER FRONTMAN 15R",
    "totalResults": "131000",
    "searchTerms": "FENDER FRONTMAN 15R",
    "count": 10,
    "startIndex": 11,
    "inputEncoding": "utf8",
    "outputEncoding": "utf8",
    "safe": "off",
    "cx": "009346898408990037447:evnb2u_eytw",
    "searchType": "image"
   }
  ],
  "request": [
   {
    "title": "Google Custom Search - FENDER FRONTMAN 15R",
    "totalResults": "131000",
    "searchTerms": "FENDER FRONTMAN 15R",
    "count": 10,
    "startIndex": 1,
    "inputEncoding": "utf8",
    "outputEncoding": "utf8",
    "safe": "off",
    "cx": "009346898408990037447:evnb2u_eytw",
    "searchType": "image"
   }
  ]
 },
 "context": {
  "title": "Digitalband.ru"
 },
 "searchInformation": {
  "searchTime": 0.108553,
  "formattedSearchTime": "0.11",
  "totalResults": "131000",
  "formattedTotalResults": "131,000"
 },
 "items": [
  {
   "kind": "customsearch#result",
   "title": "ProductWiki: Fender Frontman 15R - Guitar Amps",
   "htmlTitle": "ProductWiki: \u003cb\u003eFender Frontman 15R\u003c/b\u003e - Guitar Amps",
   "link": "http://www.productwiki.com/upload/images/fender_frontman_15r.jpg",
   "displayLink": "music.productwiki.com",
   "snippet": "Fender Frontman 15R",
   "htmlSnippet": "\u003cb\u003eFender Frontman 15R\u003c/b\u003e",
   "mime": "image/jpeg",
   "image": {
    "contextLink": "http://music.productwiki.com/fender-frontman-15r/",
    "height": 705,
    "width": 750,
    "byteSize": 187146,
    "thumbnailLink": "https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcSZqXmO_BXvT1YeawpkyKtrOZVyiUF9oyzmyzAscsYeBMpW3c6nXrd39WpA",
    "thumbnailHeight": 133,
    "thumbnailWidth": 141
   }
  },
  {
   "kind": "customsearch#result",
   "title": "Fender Frontman II 15R Guitar Combo Amp at zZounds",
   "htmlTitle": "\u003cb\u003eFender Frontman\u003c/b\u003e II \u003cb\u003e15R\u003c/b\u003e Guitar Combo Amp at zZounds",
   "link": "http://cachepe.zzounds.com/media/quality,85/0231501000_0231501049_hi-920d2ba037be602c7ac07a4d6dd353ca.jpg",
   "displayLink": "www.zzounds.com",
   "snippet": "Fender Frontman 15R Amp",
   "htmlSnippet": "\u003cb\u003eFender Frontman 15R\u003c/b\u003e Amp",
   "mime": "image/jpeg",
   "image": {
    "contextLink": "http://www.zzounds.com/item--FEN0231501049",
    "height": 1060,
    "width": 1128,
    "byteSize": 462925,
    "thumbnailLink": "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSBvCYa6wRrP_r_vhaD-bPx1bkN87hlkqzUnA0Q1EFPYO43bawkP-kJeKjcQw",
    "thumbnailHeight": 141,
    "thumbnailWidth": 150
   }
  },
  {
   "kind": "customsearch#result",
   "title": "Fender Frontman 15R photo - Elliot Lowe photos at pbase.",
   "htmlTitle": "\u003cb\u003eFender Frontman 15R\u003c/b\u003e photo - Elliot Lowe photos at pbase.",
   "link": "http://m4.i.pbase.com/o6/16/531516/1/82306224.pPVStIRJ.VE9H5117.jpg",
   "displayLink": "www.pbase.com",
   "snippet": "Fender Frontman 15R",
   "htmlSnippet": "\u003cb\u003eFender Frontman 15R\u003c/b\u003e",
   "mime": "image/jpeg",
   "image": {
    "contextLink": "http://www.pbase.com/elliot/image/82306224/original",
    "height": 683,
    "width": 1024,
    "byteSize": 263698,
    "thumbnailLink": "https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcQOlUICEeZjZdJxE8xZAJhr1IbPbdwmz73ACz7v7g6w-QQTUV2NPaM3sPo",
    "thumbnailHeight": 100,
    "thumbnailWidth": 150
   }
  },
  {
   "kind": "customsearch#result",
   "title": "Fender FM 15R.jpg",
   "htmlTitle": "\u003cb\u003eFender\u003c/b\u003e FM 15R.jpg",
   "link": "http://robbiesmusiccity.com/shop/images/Fender%20FM%2015R.jpg",
   "displayLink": "robbiesmusiccity.com",
   "snippet": "Fender Frontman 15R Series II",
   "htmlSnippet": "\u003cb\u003eFender Frontman 15R\u003c/b\u003e Series II",
   "mime": "image/jpeg",
   "image": {
    "contextLink": "http://robbiesmusiccity.com/shop/index.php?main_page=index&cPath=65_74",
    "height": 657,
    "width": 700,
    "byteSize": 184178,
    "thumbnailLink": "https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcQ5gBiEAD8LPdka50ZEJPDDKYLb2fdE_XareZA8A4Zi3mNYw0Ff-q3KIrhw",
    "thumbnailHeight": 131,
    "thumbnailWidth": 140
   }
  },
  {
   "kind": "customsearch#result",
   "title": "Clan FGI / Vanmani",
   "htmlTitle": "Clan FGI / Vanmani",
   "link": "http://clanfgi.pbworks.com/f/1215241092/Amp-pedals.jpg",
   "displayLink": "clanfgi.pbworks.com",
   "snippet": "Fender Frontman 15R.",
   "htmlSnippet": "\u003cb\u003eFender Frontman 15R\u003c/b\u003e.",
   "mime": "image/jpeg",
   "image": {
    "contextLink": "http://clanfgi.pbworks.com/w/page/13421899/Vanmani",
    "height": 600,
    "width": 800,
    "byteSize": 128009,
    "thumbnailLink": "https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcQSSkXit5NKCKZGw1aJj2MgKm9dw5yUhRboRWojumPBxyDPmyW3ojn_xpiU",
    "thumbnailHeight": 107,
    "thumbnailWidth": 143
   }
  },
  {
   "kind": "customsearch#result",
   "title": "Fender Frontman 15G | Sweetwater.",
   "htmlTitle": "\u003cb\u003eFender Frontman\u003c/b\u003e 15G | Sweetwater.",
   "link": "http://www.sweetwater.com/images/items/1800/Frontman15G-xlarge.jpg",
   "displayLink": "www.sweetwater.com",
   "snippet": "Sorry, the Fender Frontman 15G",
   "htmlSnippet": "Sorry, the \u003cb\u003eFender Frontman\u003c/b\u003e 15G",
   "mime": "image/jpeg",
   "image": {
    "contextLink": "http://www.sweetwater.com/store/detail/Frontman15G/",
    "height": 1047,
    "width": 1117,
    "byteSize": 674534,
    "thumbnailLink": "https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcTodOa6CtVXJpZnQ9nLC9AvYw1CvwIm98dZVvSSoAtoiqsXZucYUbbbSp5Mqg",
    "thumbnailHeight": 141,
    "thumbnailWidth": 150
   }
  },
  {
   "kind": "customsearch#result",
   "title": "Fender Frontman 15R | Gearnuts.",
   "htmlTitle": "\u003cb\u003eFender Frontman 15R\u003c/b\u003e | Gearnuts.",
   "link": "http://www.gearnuts.com/images/items/1800/Frontman15R-xlarge.jpg",
   "displayLink": "www.gearnuts.com",
   "snippet": "Fender Frontman 15R",
   "htmlSnippet": "\u003cb\u003eFender Frontman 15R\u003c/b\u003e",
   "mime": "image/jpeg",
   "image": {
    "contextLink": "http://www.gearnuts.com/store/detail/Frontman15R",
    "height": 1727,
    "width": 1800,
    "byteSize": 2370037,
    "thumbnailLink": "https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcTS6VS71XeWb-WflEqHNq15jb9mGan2TWjtx8DS6AVdoFi7xVULEKxeG4Mz",
    "thumbnailHeight": 144,
    "thumbnailWidth": 150
   }
  },
  {
   "kind": "customsearch#result",
   "title": "Fender Frontman 15R",
   "htmlTitle": "\u003cb\u003eFender Frontman 15R\u003c/b\u003e",
   "link": "http://www.instrumentpro.com/include/phpThumb/phpThumb.php?src=/Merchant2/graphics/00000001/fen0231501.jpg&w=300&h=300&far=1&f=jpeg&bg=FFFFFF",
   "displayLink": "www.instrumentpro.com",
   "snippet": "Fender Frontman 15R",
   "htmlSnippet": "\u003cb\u003eFender Frontman 15R\u003c/b\u003e",
   "mime": "image/",
   "fileFormat": "Image Document",
   "image": {
    "contextLink": "http://www.instrumentpro.com/p-fen0231501",
    "height": 300,
    "width": 300,
    "byteSize": 24882,
    "thumbnailLink": "https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcSp7eHL3eGwNsfMJh12_fPXTKWHMVAxC2orLkwTtyZ1p4PEPceviZfynA",
    "thumbnailHeight": 116,
    "thumbnailWidth": 116
   }
  },
  {
   "kind": "customsearch#result",
   "title": "Fender: Frontman 15R - Kirisute_Gomen's Pictures | Ultimate-",
   "htmlTitle": "\u003cb\u003eFender\u003c/b\u003e: \u003cb\u003eFrontman 15R\u003c/b\u003e - Kirisute_Gomen&#39;s Pictures | Ultimate-",
   "link": "http://profile.ultimate-guitar.com/profile_mojo_data/7/5/8/1/758183/pics/_c650015_image_0.jpg",
   "displayLink": "profile.ultimate-guitar.com",
   "snippet": "Fender: Frontman 15R. Category: Gear pics",
   "htmlSnippet": "\u003cb\u003eFender\u003c/b\u003e: \u003cb\u003eFrontman 15R\u003c/b\u003e. Category: Gear pics",
   "mime": "image/jpeg",
   "image": {
    "contextLink": "http://profile.ultimate-guitar.com/Kirisute_Gomen/pictures/gear/650005/",
    "height": 376,
    "width": 400,
    "byteSize": 76950,
    "thumbnailLink": "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRgr0AqIQygdGfXhs5cTZfwenxWCwRgSboAtD0A8cHY4mdqP222DN8nJqU",
    "thumbnailHeight": 117,
    "thumbnailWidth": 124
   }
  },
  {
   "kind": "customsearch#result",
   "title": "Scored a Frontman 15R PR241, needs a reverb tank. - Telecaster ...",
   "htmlTitle": "Scored a \u003cb\u003eFrontman 15R\u003c/b\u003e PR241, needs a reverb tank. - Telecaster \u003cb\u003e...\u003c/b\u003e",
   "link": "http://i183.photobucket.com/albums/x153/stratman323/Fender%20Frontman%20Reverb/IMG_0659.jpg",
   "displayLink": "www.tdpri.com",
   "snippet": "Scored a Frontman 15R PR241,",
   "htmlSnippet": "Scored a \u003cb\u003eFrontman 15R\u003c/b\u003e PR241,",
   "mime": "image/jpeg",
   "image": {
    "contextLink": "http://www.tdpri.com/forum/amp-central-station/217875-scored-frontman-15r-pr241-needs-reverb-tank.html",
    "height": 1024,
    "width": 989,
    "byteSize": 177389,
    "thumbnailLink": "https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcTVBr4LrWFuZOPvCpzQBx44S3-Rd_ntQUwH7qAUaicL08Bswf_0UNioB-tD",
    "thumbnailHeight": 150,
    "thumbnailWidth": 145
   }
  }
 ]
}
     """)
    Future{
      val totalResults = (json \ "searchInformation" \ "totalResults").as[String].toInt match {
        case x if x > 100 => 100
        case x => x
      }
      val items = (json \\ "items").head.as[JsArray].value
      val images = items.map {
        js =>
          new ImageSearchItem(
            (js \ "image" \ "thumbnailLink").as[String],
            (js \ "link").as[String],
            (js \ "image" \ "height").as[Int],
            (js \ "image" \ "width").as[Int]
          )
      }
      val imageList = new ListPage(pageNumber, images, totalResults)
      f(imageList)
    }
  }
}
