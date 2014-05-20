package com.example.gameuntangle;

import java.util.ArrayList;
import android.graphics.PointF;

//x个节点，最多画出(x-2)*3条 不相交的线段
public class Rope {
	private int pointNum;
	private PointF[] allPoints;
	private ArrayList<Line> lines;
	private final float pointRadius = 4f;
	private final float PowPointRadius = 16f;
	public float width, height;
	private int selectIndex = -1;

	public ArrayList<Line> getLines() {
		return lines;
	}

	public PointF[] getAllPoints() {
		return allPoints;
	}

	public int getPointNum() {
		return pointNum;
	}

	public float getPointRadius() {
		return pointRadius;
	}

	public int getSelectIndex() {
		return selectIndex;
	}

	/**
	 * 绳子
	 * @param pointNum 点数
	 * @param width 图形宽度
	 * @param height 图形高度
	 */
	public Rope(int pointNum, float width, float height) {
		if (pointNum < 3) {
			throw new RuntimeException("节点数必须大于3");
		}
		this.pointNum = pointNum;
		allPoints = new PointF[pointNum];
		this.width = width - 30;
		this.height = height - 30;
		lines = new ArrayList<Line>();
		initPoints();
		transform();
	}

	/**
	 * 初始化各个点
	 */
	public void initPoints() {
		ArrayList<Triangle> triangles = new ArrayList<Triangle>();
		PointF root = new PointF(pointRadius, pointRadius);
		float sideLength = Math.min(width, height) - pointRadius;
		PointF px = new PointF(sideLength, pointRadius);
		PointF py = new PointF(pointRadius, sideLength);
		allPoints[0] = root;
		allPoints[1] = px;
		allPoints[2] = py;
		Triangle triangle = new Triangle(0, 1, 2);
		triangles.add(triangle);
		if (pointNum > 3) {
			for (int i = 3; i < pointNum; i++) {
				double random = Math.random();
				int next = (int) (random * triangles.size());
				Triangle temp = triangles.get(next);
				PointF nextP = temp.incirclePointOfTriangle();
				Triangle newTriangle1 = new Triangle(temp.pointIndex1, temp.pointIndex2, i);
				Triangle newTriangle2 = new Triangle(temp.pointIndex2, temp.pointIndex3, i);
				Triangle newTriangle3 = new Triangle(temp.pointIndex1, temp.pointIndex3, i);
				triangles.add(newTriangle1);
				triangles.add(newTriangle2);
				triangles.add(newTriangle3);
				triangles.remove(next);
				allPoints[i] = nextP;
			}
		}
		for (Triangle t : triangles) {
			boolean flag1 = false;
			boolean flag2 = false;
			boolean flag3 = false;
			for (Line temp : lines) {
				if (t.l1.equals(temp)) {
					flag1 = true;
				}
				if (t.l2.equals(temp)) {
					flag2 = true;
				}
				if (t.l3.equals(temp)) {
					flag3 = true;
				}
				if (flag1 && flag2 && flag3) {
					break;
				}
			}
			if (!flag1) {
				lines.add(t.l1);
			}
			if (!flag2) {
				lines.add(t.l2);
			}
			if (!flag3) {
				lines.add(t.l3);
			}
		}
	}

	/**
	 * 随机变换点的坐标，使线段相交
	 */
	public void transform() {
		float max = Math.min(width, height) - pointRadius;
		for (PointF p : allPoints) {
			int distance = (int) (Math.random() * max);
			float x = Math.abs(p.x + distance - max);
			float y = Math.abs(p.y + distance - max);
			p.x = x;
			p.y = y;
		}
	}

	/**
	 * 根据接触点的坐标确定选中的点
	 * @param x
	 * @param y
	 */
	public void setSelectIndex(float x, float y) {
		for (int i = 0; i < pointNum; i++) {
			if (Math.pow((allPoints[i].x - x), 2) <= PowPointRadius
					&& Math.pow((allPoints[i].y - y), 2) <= PowPointRadius) {
				selectIndex = i;
				break;
			}
		}
	}

	/**
	 * 判断是否全部不相交
	 * @return
	 */
	public boolean win() {
		for (Line l1 : lines) {
			for (Line l2 : lines) {
				if (!l1.equals(l2)) {
					boolean b1 = intersect(l1.pointIndex1, l1.pointIndex2, l2.pointIndex1, l2.pointIndex2);
					boolean b2 = (l1.pointIndex1 == l2.pointIndex1 && !online(l1.pointIndex1, l1.pointIndex2, l2.pointIndex2))
							|| (l1.pointIndex1 == l2.pointIndex2 && !online(l1.pointIndex1, l1.pointIndex2, l2.pointIndex1))
							|| (l1.pointIndex2 == l2.pointIndex1 && !online(l1.pointIndex1, l1.pointIndex2, l2.pointIndex2))
							|| (l1.pointIndex2 == l2.pointIndex2 && !online(l1.pointIndex1, l1.pointIndex2, l2.pointIndex1));
					if (b1 && !b2) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/* 如果线段u和v相交(包括相交在端点处)时，返回true */
	public static boolean intersect(PointF p1, PointF p2, PointF p3, PointF p4) {
		return ((Math.max(p1.x, p2.x) >= Math.min(p3.x, p4.x))
				&& // 排斥实验
				(Math.max(p3.x, p4.x) >= Math.min(p1.x, p2.x))
				&& (Math.max(p1.y, p2.y) >= Math.min(p3.y, p4.y))
				&& (Math.max(p3.y, p4.y) >= Math.min(p1.y, p2.y))
				&& (multiply(p3, p2, p1) * multiply(p2, p4, p1) >= 0) && // 跨立实验
		(multiply(p1, p4, p3) * multiply(p4, p2, p3) >= 0));
	}

	/* 如果线段u和v相交(包括相交在端点处)时，返回true */
	public boolean intersect(int i1, int i2, int i3, int i4) {
		PointF p1 = allPoints[i1], p2 = allPoints[i2], p3 = allPoints[i3], p4 = allPoints[i4];
		return ((Math.max(p1.x, p2.x) >= Math.min(p3.x, p4.x))// 排斥实验
				&& (Math.max(p3.x, p4.x) >= Math.min(p1.x, p2.x))
				&& (Math.max(p1.y, p2.y) >= Math.min(p3.y, p4.y))
				&& (Math.max(p3.y, p4.y) >= Math.min(p1.y, p2.y))
				&& (multiply(p3, p2, p1) * multiply(p2, p4, p1) >= 0) && // 跨立实验
		(multiply(p1, p4, p3) * multiply(p4, p2, p3) >= 0));
	}

	/**
	 * 如果两线段相交，则两线段必然相互跨立对方。若 P1P2 跨立 Q1Q2 ，则矢量 ( P1 - Q1 ) 和 ( P2 - Q1 ) 位于矢量 (
	 * Q2 - Q1 ) 的两侧， 即 ( P1 - Q1 ) × ( Q2 - Q1 ) * ( P2 - Q1 ) × ( Q2 - Q1 ) <
	 * 0 。 上式可改写成 ( P1 - Q1 ) × ( Q2 - Q1 ) * ( Q2 - Q1 ) × ( P2 - Q1 ) > 0 。 当
	 * ( P1 - Q1 ) × ( Q2 - Q1 ) = 0 时，说明 ( P1 - Q1 ) 和 ( Q2 - Q1 ) 共线，
	 * 但是因为已经通过快速排斥试验，所以 P1 一定在线段 Q1Q2 上； 同理， ( Q2 - Q1 ) ×(P2 - Q1 ) = 0 说明 P2
	 * 一定在线段 Q1Q2 上。 所以判断 P1P2 跨立 Q1Q2 的依据是： ( P1 - Q1 ) × ( Q2 - Q1 ) * ( Q2 -
	 * Q1 ) × ( P2 - Q1 ) >= 0 。 同理判断 Q1Q2 跨立 P1P2 的依据是： ( Q1 - P1 ) × ( P2 - P1
	 * ) * ( P2 - P1 ) × ( Q2 - P1 ) >= 0 。
	 * 
	 * @param sp
	 * @param ep
	 * @param op
	 * @return
	 */
	public static double multiply(PointF sp, PointF ep, PointF op) {
		return ((sp.x - op.x) * (ep.y - op.y) - (ep.x - op.x) * (sp.y - op.y));
	}

	public double multiply(int i1, int i2, int i3) {
		PointF sp = allPoints[i1], ep = allPoints[i2], op = allPoints[i3];
		return ((sp.x - op.x) * (ep.y - op.y) - (ep.x - op.x) * (sp.y - op.y));
	}

	public static boolean online(PointF p1, PointF p2, PointF p) {
		return ((multiply(p2, p, p1) == 0) && (((p.x - p1.x) * (p.x - p2.x) <= 0) && ((p.y - p1.y)
				* (p.y - p2.y) <= 0)));
	}

	public boolean online(int i1, int i2, int i3) {
		PointF p1 = allPoints[i1], p2 = allPoints[i2], p = allPoints[i3];
		return ((multiply(p2, p, p1) == 0) && (((p.x - p1.x) * (p.x - p2.x) <= 0) && ((p.y - p1.y)
				* (p.y - p2.y) <= 0)));
	}

	public class Line {

		//端点，在allPoints中的索引
		public int pointIndex1;
		public int pointIndex2;
		public Line(int pointIndex1, int pointIndex2) {
			this.pointIndex1 = pointIndex1;
			this.pointIndex2 = pointIndex2;
		}
		@Override
		public boolean equals(Object o) {
			Line l = (Line) o;
			return (this.pointIndex1 == l.pointIndex1 && this.pointIndex2 == l.pointIndex2)
					|| (this.pointIndex2 == l.pointIndex1 && this.pointIndex1 == l.pointIndex2);
		}
	}

	public class Triangle {
		public int pointIndex1;
		public int pointIndex2;
		public int pointIndex3;
		Line l1;
		Line l2;
		Line l3;

		public Triangle(int pointIndex1, int pointIndex2, int pointIndex3) {
			this.pointIndex1 = pointIndex1;
			this.pointIndex2 = pointIndex2;
			this.pointIndex3 = pointIndex3;
			l1 = new Line(pointIndex1, pointIndex2);
			l2 = new Line(pointIndex1, pointIndex3);
			l3 = new Line(pointIndex3, pointIndex2);
		}

		private double distance(int i1, int i2) {
			PointF point1 = allPoints[i1], point2 = allPoints[i2];
			return Math.sqrt(Math.pow(point1.x - point2.x, 2)
					+ Math.pow(point1.y - point2.y, 2));
		}

		/**
		 * 三角形内切圆的圆点
		 * @return
		 */
		public PointF incirclePointOfTriangle() {
			PointF point1 = allPoints[pointIndex1], point2 = allPoints[pointIndex2], point3 = allPoints[pointIndex3];
			PointF center = new PointF();
			double a, b, c;
			double xa, ya, xb, yb, xc, yc;

			a = distance(pointIndex1, pointIndex2);
			b = distance(pointIndex2, pointIndex3);
			c = distance(pointIndex3, pointIndex1);

			xa = point1.x;
			ya = point1.y;

			xb = point2.x;
			yb = point2.y;

			xc = point3.x;
			yc = point3.y;

			center.x = (float) ((a * xa + b * xb + c * xc) / (a + b + c));
			center.y = (float) ((a * ya + b * yb + c * yc) / (a + b + c));
			return center;
		}
	}
}
