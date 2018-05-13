package com.example.pc.myapplication;

public class Region extends MainActivity {

    private int xPointToCompareWith, yPointToCompareWith;

    int[]	R_1	=	{	200	,	620	,	416	,	780	};
    int[]	R_2	=	{	200	,	620	,	810	,	705	};
    int[]	R_3	=	{	561	,	462	,	624	,	1177};
    int[]	R_4	=	{	754	,	472	,	821	,	1053};
    int[]	R_5	=	{	561	,	960	,	862	,	1023};
    int[]	R_6	=	{	741	,	948	,	861	,	1049};
    int[]	R_7	=	{	0	,	470	,	213	,	754};

    private void setPoints(int x, int y){
        xPointToCompareWith = x;
        yPointToCompareWith = y;
    }

    private float sign(float pointXFirst, float pointYFirst, float pointXSecond, float pointYSecond, float pointXThird, float pointYThird)
    {
        return (pointXFirst - pointXThird) * (pointYSecond - pointYThird) - (pointXSecond - pointXThird) * (pointYFirst - pointYThird);
    }

    private boolean isInTriangle(float pointXFirst, float pointYFirst, float pointXSecond, float pointYSecond, float pointXThird, float pointYThird)
    {
        boolean rect1, rect2, rect3;
        rect1 = sign(xPointToCompareWith, yPointToCompareWith, pointXFirst, pointYFirst,  pointXSecond, pointYSecond) < 0.0f;
        rect2 = sign(xPointToCompareWith, yPointToCompareWith, pointXSecond, pointYSecond, pointXThird, pointYThird) < 0.0f;
        rect3 = sign(xPointToCompareWith, yPointToCompareWith, pointXThird, pointYThird, pointXFirst, pointYFirst) < 0.0f;
        return ((rect1 == rect2) && (rect2 == rect3));
    }

    private boolean checkOneRegion(int[] a){
        boolean f,s;
        f = isInTriangle(a[0], a[1], a[2], a[1], a[0], a[3]);
        s = isInTriangle(a[2], a[3], a[2], a[1], a[0], a[3]);
        return (f || s);
    }

    public boolean validate(int x, int y){
        setPoints(x,y);
        return  checkOneRegion(R_1) || checkOneRegion(R_2) || checkOneRegion(R_3) || checkOneRegion(R_4) || checkOneRegion(R_5) || checkOneRegion(R_6) || checkOneRegion(R_7);
    }

}