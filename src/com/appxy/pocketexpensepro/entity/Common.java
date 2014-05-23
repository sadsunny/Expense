package com.appxy.pocketexpensepro.entity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

import com.appxy.pocketexpensepro.R;

public class Common {
	
	public static int CURRENCY = 148;
	
	public final static int[] ExpenseColors = new int[] { 
		Color.argb(255, 246, 90, 70),
		Color.argb(255, 239, 48, 48),
		Color.argb(255, 199, 34, 29),
		
		Color.argb(255, 210, 113, 54),
		Color.argb(255, 255, 172, 48),
		Color.argb(255, 255, 208, 54),
		
		Color.argb(255, 255, 241, 83),
		Color.argb(255, 152, 102,153),
		Color.argb(255, 137, 55, 139),
		Color.argb(255, 150, 25, 101),
	};// expense Color
	
	public final static int[] IncomeColors = new int[] { 
		Color.argb(255, 111, 182, 14),
		Color.argb(255, 59, 214, 77),
		Color.argb(255, 0, 183, 92),
		
		Color.argb(255, 14, 160, 104),
		Color.argb(255, 19, 96, 85),
		Color.argb(255, 253, 254, 175),
		
		Color.argb(255, 224, 207, 127),
		Color.argb(255, 190, 154,58),
		Color.argb(255, 222, 173, 60),
		Color.argb(255, 254, 185, 38),
	};// income Color
	
	
	public static String turnToDate(long mills) {

		Date date2 = new Date(mills);
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy, EEE HH:mm:ss");
		String theDate = sdf.format(date2);
		return theDate;
	}
	
	public static String getRealPathFromURI(Uri contentUri, Context context) {
		String[] proj = { MediaStore.Images.Media.DATA };
		CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
		Cursor cursor = loader.loadInBackground();
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	
	public final static Integer[] ACCOUNT_TYPE_ICON = { R.drawable.icon_baby,
			R.drawable.icon_car, R.drawable.icon_computer,
			R.drawable.icon_cosmetology, R.drawable.icon_credit_card,
			R.drawable.icon_dining, R.drawable.icon_education,
			R.drawable.icon_ele, R.drawable.icon_gas };


	public final static String[] CURRENCY_SIGN = { "Lek", "Kz", "$", "դր",
			"Afl.", "$", "AZN", "د.ج", "؋", "B$", "৳", "Bds$", "BYR", "$",
			"BD$ ", "Nu.", "Bs", "KM", "P", "R$", "£", "$", "лв.", "FBu",
			".د.ب", "$", "$", "$", "CFA", "FCFA", "F", "$", "￥", "$", "CF",
			"F", "₡", "Kn", "$MN", "Kč", "؋", "kr", "$", "RD$", "$", "$", "kr",
			"$", "€", "ج.م", "£", "FJ$", "D", "GEL", "GH¢", "£", "Q", "FG",
			"F$", "G", "L", "$", "Ft", "kr.", "Rs.", "Rp", "₪", "﷼", "ع.د",
			"J$", "￥", "د.ا ", "〒", "KSh", "KGS", "د.ك", "₭", "Ls", "L", "L$",
			"Lt", "ل.ل", "ل.د", "MOP", "MDen", "MGA", "MK", "RM", "MRf", "R",
			"UM", "$", "MDL", "₮", "MTn", "K", "د.م.", "N$", "रू.", "ƒ", "NT$",
			"$", "₦", "C$", "₩", "kr", "ر.ع.", "PKR", "PAB", "K", "PYG", "PEN",
			"₱", "zł", "£", "ر.ق", "lei", "руб.", "RF", "£", "Db", "RSD", "SR",
			"Le", "$", "SI$ ", "$", "₩", "R", "SL Re", "SDG", "$", "L", "kr",
			"SFr.", "ل.س", "ر.س", "TJS", "TSh", "฿", "T$", "TT$", "TRY",
			"د.ت ", "USh", "rpH.", "COU", "$U", "$", "so‘m", "د.إ", "Vt",
			"BsF", "₫", "WS$", "﷼", "ZK" };

	public final static String[] CURRENCY_NAME = { "Albanian Lek",
			"Angolan Kwanza", "Argentine Peso", "Armenian Dram",
			"Aruban Florin", "Australian Dollar", "Azerbaijanian Manat",
			"Algerian Dinar", "Afghan Afghani", "Bahamian Dollar",
			"Bangladeshi Taka", "Barbadian Dollar", "Belarusian Ruble",
			"Belize Dollar", "Bermudan Dollar", "Bhutanese Ngultrum",
			"Bolivian Boliviano", "Bosnia-Herzegovina", "Botswanan Pula",
			"Brazilian Real", "British Pound Sterling", "Brunei Dollar",
			"Bulgarian Lev", "Burundian Franc", "Bahraini Dinar",
			"Canadian Dollar", "Cape Verde Escudo", "Cayman Islands Dollars",
			"CFA Franc BCEAO", "CFA Franc BEAC", "CFP Franc", "Chilean Peso",
			"Chinese Yuan Renminbi", "Colombian Peso", "Comorian Franc",
			"Congolese Franc", "Costa Rican colón", "Croatian Kuna",
			"Cuban Peso", "Czech Republic Koruna", "Cambodian Riel",
			"Danish Krone", "Djiboutian Franc", "Dominican Peso",
			"East Caribbean Dollar", "Eritrean Nakfa", "Estonian Kroon",
			"Ethiopian Birr", "Euro", "Egyptian Pound",
			"Falkland Islands Pound", "Fijian Dollar", "Gambia Dalasi",
			"Georgian Lari", "Ghanaian Cedi", "Gibraltar Pound",
			"Guatemalan Quetzal", "Guinean Franc", "Guyanaese Dollar",
			"Haitian Gourde", "Honduran Lempira", "Hong Kong Dollar",
			"Hungarian Forint", "Icelandic króna", "Indian Rupee",
			"Indonesian Rupiah", "Israeli New Sheqel", "lraqi Dinar",
			"Iranian Rial", "Jamaican Dollar", "Japanese Yen",
			"Jordanian Dinar", "Kazakhstani Tenge", "Kenyan Shilling",
			"Kyrgystani Som", "Kuwaiti Dinar", "Laotian Kip", "Latvian Lats",
			"Lesotho Loti", "Liberian Dollar", "Lithuanian Litas",
			"Lebanese Pound", "Libyan Dinar", "Macanese Pataca",
			"Macedonian Denar", "Madagascar Ariary", "Malawian Kwacha",
			"Malaysian Ringgit", "Maldive Islands Rufiyaa", "Mauritian Rupee",
			"Mauritanian Ouguiya", "Mexican Peso", "Moldovan Leu",
			"Mongolian Tugrik", "Mozambican Metical", " Myanma Kyat",
			"Moroccan Dirham", "Namibian Dollar", "Nepalese Rupee",
			"Netherlands Antillean Guilder", "New Taiwan Dollar",
			"New Zealand Dollar", "Nigerian Naira", "Nicaraguan Cordoba Oro",
			"North Korean Won", "Norwegian Krone", "Omani Rial",
			"Pakistani Rupee", "Panamanian Balboa", "Papua New Guinea Kina",
			"Paraguayan Guarani", "Peruvian Nuevo Sol", "Philippine Peso",
			"Polish Zloty", "Pound", "Qatari Rial", "Romanian Leu",
			"Russian Ruble", "Rwandan Franc", "Saint Helena Pound",
			"São Tomé and Príncipe", "Serbian Dinar", "Seychelles Rupee",
			"Sierra Leonean Leone", "Singapore Dollar",
			"Solomon Islands Dollar", "Somali Shilling", "Sorth Korean Won",
			"South African Rand", "Sri Lanka Rupee", "Sudanese Pound",
			"Surinamese Dollar", "Swazi Lilangeni", "Swedish Krona",
			"Swiss Franc", "Saudi Riyal", "Syrian Pound", "Tajikistani Somoni",
			"Tanzanian Shilling", "Thai Baht", "Tonga Pa???anga",
			"Trinidad and Tobago", "Turkish Lira", "Tunisian Dinar",
			"Ugandan Shilling", "Ukrainian Hryvnia", "Unidad de Valor Real",
			"Uruguay Peso", "US Dollar", "Uzbekistan Som",
			"United Arab Emirates", "Vanuatu Vatu",
			"Venezuelan Bolivar Fuerte", "Vietnamese Dong", "Samoabn Tala",
			"Yemeni Rial", "Zambian Kwacha" };
}
