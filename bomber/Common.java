/*  Bomber for Nokia Series 60 Phones
    Copyright (C) 2003, 2004  While True, d.o.o.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
	
    For any info contact gorazd@whiletrue.com.
*/

/*==========================================================================;
 *
 *  While True, d.o.o.
 *	
 *  File:       common.java
 *  Content:    Common J2ME game functions
 *  Created:    October 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/


package bomber;


// =========================================================================;
//	Name:	class Common
//	Desc:	returns fixed point cosine of argument. Argument angle is
//              given in fixed point degrees. Angle must be in range 
//              0..360 * FIXED.
//
// ==========================================================================;
public class Common 
{
public final static int FIXED = 1024;       // DO NOT CHANGE! Code is optimized for this number
private final static int[] c_sin_table =    // sinus table, used for sin and cos funtions
        {0,
        12,
        25,
        37,
        50,
        62,
        75,
        87,
        100,
        112,
        125,
        137,
        150,
        162,
        175,
        187,
        199,
        212,
        224,
        236,
        248,
        260,
        273,
        285,
        297,
        309,
        321,
        333,
        344,
        356,
        368,
        380,
        391,
        403,
        414,
        426,
        437,
        449,
        460,
        471,
        482,
        493,
        504,
        515,
        526,
        537,
        547,
        558,
        568,
        579,
        589,
        599,
        609,
        620,
        629,
        639,
        649,
        659,
        668,
        678,
        687,
        696,
        706,
        715,
        724,
        732,
        741,
        750,
        758,
        767,
        775,
        783,
        791,
        799,
        807,
        814,
        822,
        829,
        837,
        844,
        851,
        858,
        865,
        871,
        878,
        884,
        890,
        897,
        903,
        908,
        914,
        920,
        925,
        930,
        936,
        941,
        946,
        950,
        955,
        959,
        964,
        968,
        972,
        976,
        979,
        983,
        986,
        990,
        993,
        996,
        999,
        1001,
        1004,
        1006,
        1008,
        1010,
        1012,
        1014,
        1016,
        1017,
        1019,
        1020,
        1021,
        1022,
        1022,
        1023,
        1023,
        1023,
        1024};
     
        
// =========================================================================;
//	Name:	sin(int angle)
//	Desc:	returns fixed point sine of argument. Argument angle is
//              given in fixed point degrees. Angle should be in range 
//              0..360 * FIXED.
//
// ==========================================================================;

    static public int sin(int angle)
    {
        //assert (angle >= 0 && angle < 360 * FIXED);
        angle %= 360 * FIXED;
        if (angle < 0) angle += 360 << 10;
        
        byte sign = 1;
        if (angle > (270 << 10)) 
        {
	    angle = (360 << 10) - angle;
            sign = -1;
	}
        else if (angle > (90 << 10))
        {
            if (angle <= (180 << 10))
            {
                angle = (180 << 10) - angle;
                //sign = -1;
            } 
            else if (angle <= (270 << 10))
            {
                angle = angle - (180 << 10);
                sign = -1;
            } 
        }
        return sign * c_sin_table[((int)angle >> 3)/90];
    }
// =========================================================================;
//	Name:	cos(int angle)
//	Desc:	returns fixed point cosine of argument. Argument angle is
//              given in fixed point degrees. Angle should be in range 
//              0..360 * FIXED.
//
// ==========================================================================;
 
    static public int cos(int angle)
    {
        angle = angle + (90 << 10);
        return sin(angle);
    }
        
    public static int toLong (int  x) 
    {
        return x >> 10;
    }

    public static int toInt (int  x) 
    {
        return (int)(x >> 10);
    }
    
    public static int ceilInt (int  x) 
    {
        if (x % FIXED != 0) return (int)((x >> 10) + 1);
        else return (int)(x >> 10);
    }
    
    public static int roundInt (int  x) 
    {
        if (x % FIXED > FIXED/2) return (int)((x >> 10) + 1);
        else return (int)(x >> 10);
    }
        
     public static byte toByte (int x) 
    {
        return (byte)(x >> 10);
    }
    
    public static int toFP (int x) 
    {
	return x << 10;
    }

    public static int mul (long x, long y) 
    {
	return (int)(x * y >> 10);
    }

    public static int fastMul (int x, int y) 
    {
	return (x * y) >> 10;
    }

    
     public static int mod (int x, int y) 
    {
	return x % y;
    }
    
    
    public static int div (int x, int y) 
    {
	return (int)(((long)x << 20) / y) >> 10;
    }
  
    static final byte COLLINEAR = 1;
    static final byte DONT_INTERSECT = 2;
    static final byte DO_INTERSECT = 0;
    
    private static boolean SAME_SIGNS(int a, int b)
    {
        if (a >= 0 && b >= 0 || a <=0 && b <= 0) return true;
        else return false;
    }
    
    // WARN ME: VERY SLOW!!!!
    public static int sqrt (long n) 
    {
        if (n <= 0) return 0;
        long s = (n + 1024) >> 1;
        for (int i = 0; i < 8; i++) 
        {
            s = (s + (((n << 20) / s) >> 10)) >> 1;
        }
        return (int)s;
    }
    
    public static int sqr(long n) 
    {
        return (int)((n * n) >> 10);
    }
    
// =========================================================================;
//	Name:	int vectorToAngle(int x, int y)
//	Desc:	returns fixed point angle [0..360] which points in direction
//              of vector [x, y]. We are using mathematically correct
//              orientation, so 0 deg is right, 90 is down, 180 is left and
//              270 is left.
// ==========================================================================;
    
    public static int vectorToAngle(int x, int y)
    {
        int len = sqrt(sqr(x) + sqr(y));
        boolean sign = true;
        int add = 0;

        if (x > 0)
        {
            if (y < 0) 
            {
                add = 270 * FIXED;
                sign = false;
            }
            else
            {
                //add = 0; already initialized to that value
            }
        }
        else if (x < 0)
        {
            
            if (y < 0) 
            {
                add = 180 * FIXED;
            }
            else 
            {
                sign = false;
                add = 90 * FIXED;
            }
        }
        else 
        {
            if (y > 0) return 90 * FIXED;
            else return 270 * FIXED;
        }
        
        int cos = div(Math.abs(x), len);
        short min = 0;
        short max = 128;
        while (true)
        {
            if (c_sin_table[(min + max) / 2] < cos)
            {
                min = (short)((min + max) / 2);
            }
            else
            {
                max = (short)((min + max) / 2);
            }
            if (max - min <= 1)
            {
                if (c_sin_table[min] > cos ) min = max;
                if (sign) return FIXED * 90 * (128 - min) / 128 + add;
                else return FIXED * 90 * min / 128 + add;
                
            }
        }
    }
// =========================================================================;
//	Name:	int clampAngle(int angle)
//	Desc:	clamps given angle to interval 0..360 * FIXED
// ==========================================================================;
    
    public static int clampAngle(int angle)
    {
        angle %= 360 * Common.FIXED;
        if (angle < 0) angle += 360 * Common.FIXED;
        return angle;
    }
    
// =========================================================================;
//	Name:	int distancePointToLine(int x, int y, int vx, int vy)
//	Desc:	returns distance from given point to (given) infinite line.
//              Line is given as vector.
//              NOTE: result is signed, depending on which side of the
//              line, the point is.            
// ==========================================================================;
    
    public static int distancePointToLine(int x, int y, int vx, int vy)
    {
        //d = (x2 - x1)(y1 - y0) - (x1 - x0)(y2 - y1)
        //    ----
        //    len(vx, vy)
        //System.out.println("x = "+ Long.toString(x)  + ",y = "+ Long.toString(y)+ ",vx = "+ Long.toString(vx)+",vy = "+ Long.toString(vy) );
        
        int dist = Common.sqrt(Common.sqr(vx) + Common.sqr(vy));
        return Common.div((-Common.mul(vx, y) + Common.mul(x,vy)), dist);
    }
    
    
// =========================================================================;
//	Name:	byte segmentsIntersect( int x1, int y1, int x2, int y2, 
//                                      int x3, int y3, int x4, int y4, 
//                                      Point result)
//	Desc:	Calculates intersection between two segments. All 
//              coordinates are given in fixed point numbers.
//              returns DONT_INTERSECT if segments do not intersect,
//              returns DO_INTERSECT if segments intersect and returns
//              point of intersection in result.
//              Returns COLLINEAR if segments are collinear.
//              given in fixed point degrees. 
//              This code is from Graphics Gems.
//
// ==========================================================================;
    public static byte segmentsIntersect( int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4, Point result)
    {
        int a1, a2, b1, b2, c1, c2; /* Coefficients of line eqns. */
        int r1, r2, r3, r4;         /* 'Sign' values */
        int denom, offset, num;     /* Intermediate values */

        /* Compute a1, b1, c1, where line joining points 1 and 2
         * is "a1 x  +  b1 y  +  c1  =  0".
         */

        a1 = y2 - y1;
        b1 = x1 - x2;
        c1 = mul(x2, y1) - mul(x1, y2);

        /* Compute r3 and r4.
         */


        r3 = mul(a1, x3) + mul(b1, y3) + c1;
        r4 = mul(a1, x4) + mul(b1, y4) + c1;

        /* Check signs of r3 and r4.  If both point 3 and point 4 lie on
         * same side of line 1, the line segments do not intersect.
         */

        if ( r3 != 0 && r4 != 0 &&  SAME_SIGNS( r3, r4 )) return DONT_INTERSECT ;

        /* Compute a2, b2, c2 */

        a2 = y4 - y3;
        b2 = x3 - x4;
        c2 = mul(x4, y3) - mul(x3, y4);

        /* Compute r1 and r2 */

        r1 = mul(a2, x1) + mul(b2,  y1) + c2;
        r2 = mul(a2, x2) + mul(b2, y2) + c2;

        /* Check signs of r1 and r2.  If both point 1 and point 2 lie
         * on same side of second line segment, the line segments do
         * not intersect.
         */

        if ( r1 != 0 && r2 != 0 &&  SAME_SIGNS( r1, r2 )) return DONT_INTERSECT;

        /* Line segments intersect: compute intersection point. 
         */

        denom = mul(a1, b2) - mul(a2, b1);
        if ( denom == 0 ) return COLLINEAR;
        offset = denom < 0 ? - denom / 2 : denom / 2;

        /* The denom/2 is to get rounding instead of truncating.  It
         * is added or subtracted to the numerator, depending upon the
         * sign of the numerator.
         */

        num = mul(b1,  c2) - mul (b2,  c1);
        result.x = ( num < 0 ? num - offset : num + offset ) / denom;

        num = mul(a2, c1) - mul(a1, c2);
        result.y = ( num < 0 ? num - offset : num + offset ) / denom;

        return DO_INTERSECT;
        } /* lines_intersect */

}



