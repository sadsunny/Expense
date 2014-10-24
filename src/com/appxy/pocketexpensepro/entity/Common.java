package com.appxy.pocketexpensepro.entity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import android.R.integer;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.util.Log;

import com.appxy.pocketexpensepro.R;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

public class Common {

	public static int CURRENCY = 148;
	// Does the user paid?
	public static boolean mIsPaid = false;
	

	public static int getName2Identity(Context context, String name) { // 文件名转化成ID
		int resId = R.drawable.ic_logo;
		try {
			resId = context.getResources().getIdentifier(name, "drawable",
					"com.appxy.pocketexpensepro");
		} catch (Exception e) {
			// TODO: handle exception
			resId = R.drawable.ic_logo;
		}
		return resId;
	}

	public static String getID2Name(Context context, int id) { //Id转化为文件名
		String imgName = "ic_logo"; 
		try {
			imgName = context.getResources().getResourceName(id).split("/")[1];
		} catch (Exception e) {
			// TODO: handle exception
			imgName = "ic_logo"; 
		}
		return imgName+".png";
	}
	
	public final static String[] ACCOUNTTYPESYNCNAME = {
		"asset.png",
		"cash.png",
		"checking.png",
		"credit-card.png",
		"Debt.png",
		"investing.png",
		"loan.png",
		"Saving.png",
		"icon_other.png"
	};
	
	public static int positionAccountType(String iconName) {
		
		for (int i = 0; i < ACCOUNTTYPESYNCNAME.length; i++) {
			if ( ACCOUNTTYPESYNCNAME[i].equals(iconName) ) {
				return i;
			}
		}
		
		return 0;
		
	}
	
	
	public final static Integer[] ACCOUNT_TYPE_ICON = {
			R.drawable.asset_type,
			R.drawable.cash_type, 
			R.drawable.checking_type,
			R.drawable.credit_card_type,
			R.drawable.debit_card_type,
			R.drawable.investing_type, 
			R.drawable.loan_type,
			R.drawable.savings_type, 
			R.drawable.wenhao_type

	};

	public final static Integer[] ACCOUNT_TYPE_ICON_SELECTOR = {
			R.drawable.asset_type_selector, R.drawable.cash_type_selector,
			R.drawable.checking_type_selector,
			R.drawable.credit_card_type_selector,
			R.drawable.debit_card_type_selector,
			R.drawable.investing_type_selector, R.drawable.loan_type_selector,
			R.drawable.savings_type_selector, R.drawable.wenhao_type_selector

	};

	public final static int[] ExpenseColors = new int[] {
			Color.argb(255, 246, 90, 70), Color.argb(255, 239, 48, 48),
			Color.argb(255, 199, 34, 29),

			Color.argb(255, 210, 113, 54), Color.argb(255, 255, 172, 48),
			Color.argb(255, 255, 208, 54),

			Color.argb(255, 255, 241, 83), Color.argb(255, 152, 102, 153),
			Color.argb(255, 137, 55, 139), Color.argb(255, 150, 25, 101), };// expense
																			// Color

	public final static int[] IncomeColors = new int[] {
			Color.argb(255, 111, 182, 14), Color.argb(255, 59, 214, 77),
			Color.argb(255, 0, 183, 92),

			Color.argb(255, 14, 160, 104), Color.argb(255, 19, 96, 85),
			Color.argb(255, 253, 254, 175),

			Color.argb(255, 224, 207, 127), Color.argb(255, 190, 154, 58),
			Color.argb(255, 222, 173, 60), Color.argb(255, 254, 185, 38), };// income
																			// Color

	public static String turnToDate(long mills) {

		Date date2 = new Date(mills);
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy, EEE HH:mm:ss");
		String theDate = sdf.format(date2);
		return theDate;
	}

	public static String getRealPathFromURI(Uri contentUri, Context context) {
		String[] proj = { MediaStore.Images.Media.DATA };
		CursorLoader loader = new CursorLoader(context, contentUri, proj, null,
				null, null);
		Cursor cursor = loader.loadInBackground();
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	public final static String [] CATEGORYSYNCNAME = {
		"airplane.png",
		"icon_apple.png",
		"house_hold.png",
		"mortgage.png",
		"icon_auto_gas.png",
		"icon_auto2.png",
		"icon_bag.png",
		"icon_ball.png",
		"bank_charge.png",
		"icon_bill.png",
		
		"bonus.png",
		"icon_book.png", 
		"travel.png", 
		"icon_business.png", 
		"icon_cable_TV.png", 
		"auto.png",
		"category_cash.png", 
		"icon_ceremony.png",
		"charity.png",
		"childcare.png",
		
		"clothing.png",
		"icon_commonfood.png",
		"icon_comp.png",
		"icon_cosmetics.png",
		"icon_credit_card.png",
		"credit_card-payment.png",
		"Garbage-&-Recycling.png",
		"icon_digital_product.png",
		"dinning.png",
		"education.png",
		
		"entertainment.png",
		"icon_fastfood.png",
		"icon_favorite.png",
		"icon_furniture.png",
		"icon_game.png",
		"gifts.png",
		"groceries.png",
		"health_fitness.png",
		"icon_health_fitness.png",
		"icon_health_fitness4.png",
		
		"icon_heart.png",
		"icon_hobby.png",
		"utilities.png",
		"insurance.png",
		"interent.png",
		"category_loan.png",
		"icon_lunch.png",
		"medical.png", 
		"icon_mind.png",
		"misc.png",
		
		"icon_movie.png",
		"my_kids.png",
		"my_pets.png",
		"icon_party.png",
		"my_pets2.png",
		"icon_power.png",
		"uncategorized.png",
		"uncategorized_income.png",
		"rent.png",
		"homerepair.png",
		
		"salary.png",
		"icon_Salary2.png",
		"savings-deposit.png",
		"icon_star.png",
		"tax-fed.png",
		"icon_tax2.png",
		"icon_tea.png",
		"con_teeth.png",
		"icon_traffic_other.png",
		"iocn_transfer.png",
		
		"utilities_gas.png",
		"icon_vegatable.png",
		"icon_vocation.png",
		"water.png",
		"icon_wedding.png",
		"IRA-trib.png"
	};
	
	public static int positionCategory(String cName) {
		
		if ( cName.equals("auto_gas.png")|| cName.equals("auto_resiger.png") || cName.equals("auto_service.png")) {
			return 16-1;
		}
		 if (cName.equals("cable-TV.png")) {
			return 15-1;
		}
		 
		 if (cName.equals("house_hold2.png")) {
			 return 43-1;
		}
		 
		 if (cName.equals("telephone.png")) {
			 return 2-1;
		}
		 
		 if (cName.equals("icon_entertainment.png")) {
			 return 31;
		}
		 
		 if (cName.equals("icon_utilities_gas.png") || cName.equals("utility_gas.png")) {
			 return 71;
		 }
		 
		 if ( cName.equals("medicare.png") || cName.equals("SDI.png") || cName.equals("soc-sec.png") || cName.equals("tax-other.png") || cName.equals("tax-property.png") || cName.equals("tax-state.png") ) {
			 return 65-1;
		}
		
		for (int i = 0; i < CATEGORYSYNCNAME.length; i++) {
			if ( CATEGORYSYNCNAME[i].equals(cName) ) {
				return i;
			}
		}
		
		return 0;
		
	}

	
	public final static Integer[] CATEGORY_ICON = {
		    R.drawable.airplane,
			R.drawable.apple, 
			R.drawable.appliances, 
			R.drawable.asset,
			R.drawable.auto_gas, 
			R.drawable.auto2,
			R.drawable.bag,
			R.drawable.ball, 
			R.drawable.bank,
			R.drawable.bill,
			
			R.drawable.bonus,
			R.drawable.book,
			R.drawable.bus,
			R.drawable.business,
			R.drawable.cable_tv,
			R.drawable.car,
			R.drawable.category_cash, 
			R.drawable.ceremony, 
			R.drawable.charity,
			R.drawable.childcare, 
			
			R.drawable.clothing, 
			R.drawable.commonfood,
			R.drawable.comp,
			R.drawable.cosmetics, 
			R.drawable.credit_card2,
			R.drawable.credit_card, 
			R.drawable.delete,
			R.drawable.digital_product,
			R.drawable.eat, 
			R.drawable.education,
			
			R.drawable.entertainment, 
			R.drawable.fastfood, 
			R.drawable.favorite,
			R.drawable.furniture, 
			R.drawable.game,
			R.drawable.gifts,
			R.drawable.grocery,
			R.drawable.health_fitness,
			R.drawable.health_fitness3, 
			R.drawable.health_fitness4,
			
			R.drawable.heart,
			R.drawable.hobby, 
			R.drawable.house_hold2,
			R.drawable.insurance,
			R.drawable.interent, 
			R.drawable.loan,
			R.drawable.lunch,
			R.drawable.medicare, 
			R.drawable.mind,
			R.drawable.misc, 
			
			R.drawable.movie,
			R.drawable.my_kids,
			R.drawable.my_pets,
			R.drawable.party,
			R.drawable.pets,
			R.drawable.power, 
			R.drawable.question_mark, //57 
			R.drawable.question_mark2,
			R.drawable.rent,
			R.drawable.repair,
			
			R.drawable.salary, 
			R.drawable.salary2, 
			R.drawable.saving,
			R.drawable.star,
			R.drawable.tax,
			R.drawable.tax2,
			R.drawable.tea,
			R.drawable.teeth, 
			R.drawable.traffic_other,
			R.drawable.transfer,
			
			R.drawable.utilities_gas,
			R.drawable.vegatable,
			R.drawable.vocation,
			R.drawable.water, 
			R.drawable.wedding,
			R.drawable.ira_trib };

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
