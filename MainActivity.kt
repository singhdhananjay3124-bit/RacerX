package com.racerx.game

import android.app.Activity
import android.os.Bundle
import android.graphics.*
import android.view.*
import kotlin.math.min
import kotlin.random.Random

class MainActivity: Activity(){
 override fun onCreate(b:Bundle?){super.onCreate(b);setContentView(GameView())}
 inner class GameView:View(this){
  val p=Paint(1); var state=0; var score=0; var coins=0; var high=0
  var carX=.5f; var nitro=100f; var roadOffset=0f; var last=System.currentTimeMillis()
  data class Enemy(var lane:Int,var y:Float,var speed:Float)
  val enemies=mutableListOf<Enemy>()

  init{p.typeface=Typeface.create("sans-serif",Typeface.BOLD); reset()}
  fun reset(){state=1;score=0;coins=0;nitro=100f;carX=.5f;enemies.clear();repeat(4){enemies.add(Enemy(Random.nextInt(3),-300f-it*330f,7f+Random.nextFloat()*3))}}
  override fun onDraw(c:Canvas){
   val w=width.toFloat();val h=height.toFloat(); val now=System.currentTimeMillis()
   val dt=((now-last).coerceAtMost(50))/16f;last=now
   c.drawColor(Color.rgb(8,12,22))
   p.color=Color.rgb(22,42,68);c.drawRect(0f,0f,w,h*.22f,p)
   p.color=Color.rgb(48,48,53);c.drawRect(w*.07f,h*.18f,w*.93f,h,p)
   p.color=Color.CYAN;c.drawRect(w*.07f,h*.18f,w*.075f,h,p);c.drawRect(w*.925f,h*.18f,w*.93f,h,p)
   p.color=Color.WHITE
   roadOffset=(roadOffset+10*dt)%150
   for(l in 1..2){val x=w*(.07f+.86f*l/3);var y=-150+roadOffset;while(y<h){c.drawRoundRect(x-4,y,x+4,y+70,4f,4f,p);y+=150}}
   if(state==1){
    score+=(dt/2).toInt(); high=maxOf(high,score); nitro=min(100f,nitro+dt*.5f)
    enemies.forEach{it.y+=(it.speed+score/500f)*dt*if(nitro>99f&&false)2f else 1f}
    if(enemies.count{it.y>h+100}==0 && Random.nextFloat()<.025f) enemies.add(Enemy(Random.nextInt(3),-180f,8f+Random.nextFloat()*4))
    enemies.removeAll{it.y>h+200}
    drawEnemies(c,w)
    val pw=w*.20f; val px=w*(.07f+.86f*carX)-pw/2
    drawCar(c,px,h*.78f,pw,145f,false)
    val me=RectF(px,h*.78f,px+pw,h*.78f+145)
    for(e in enemies){val ex=w*(.07f+.86f*(e.lane+.5f)/3)-pw/2;val er=RectF(ex,e.y,ex+pw,e.y+145);if(RectF.intersects(me,er)){state=2;break}}
   } else if(state==0){menu(c,w,h)} else {drawEnemies(c,w);gameOver(c,w,h)}
   p.color=Color.WHITE;p.textSize=40f;c.drawText("SCORE $score",25f,55f,p)
   if(state==1){p.textSize=28f;c.drawText("🪙 $coins",25f,92f,p);p.textSize=22f;c.drawText("NITRO",w-145,35f,p);p.color=Color.CYAN;c.drawRoundRect(w-145,45f,w-25,58f,7f,7f,p);p.color=Color.WHITE;c.drawText("DRAG TO STEER",25f,h-25,p);postInvalidateOnAnimation()}
  }
  fun drawEnemies(c:Canvas,w:Float){val pw=w*.20f;for(e in enemies){val x=w*(.07f+.86f*(e.lane+.5f)/3)-pw/2;drawCar(c,x,e.y,pw,145f,true)}}
  fun drawCar(c:Canvas,x:Float,y:Float,w:Float,h:Float,enemy:Boolean){
   p.color=if(enemy)Color.rgb(255,115,25) else Color.rgb(230,25,55);c.drawRoundRect(x,y,x+w,y+h,20f,20f,p)
   p.color=Color.rgb(25,30,42);c.drawRoundRect(x+w*.17f,y+h*.16f,x+w*.83f,y+h*.45f,14f,14f,p)
   p.color=Color.WHITE;c.drawRoundRect(x+w*.10f,y+h*.05f,x+w*.90f,y+h*.10f,4f,4f,p)
   p.color=Color.BLACK;c.drawCircle(x+w*.13f,y+h*.78f,w*.11f,p);c.drawCircle(x+w*.87f,y+h*.78f,w*.11f,p)
  }
  fun menu(c:Canvas,w:Float,h:Float){p.color=Color.WHITE;p.textSize=70f;c.drawText("RACER X",w*.17f,h*.30f,p);p.textSize=30f;p.color=Color.CYAN;c.drawText("MODERN STREET RACING",w*.16f,h*.36f,p);p.color=Color.WHITE;c.drawRoundRect(w*.25f,h*.53f,w*.75f,h*.63f,25f,25f,p);p.color=Color.BLACK;p.textSize=34f;c.drawText("START",w*.38f,h*.595f,p);p.color=Color.WHITE;p.textSize=25f;c.drawText("BEST SCORE: $high",w*.30f,h*.70f,p)}
  fun gameOver(c:Canvas,w:Float,h:Float){p.color=0xDD000000.toInt();c.drawRect(0f,0f,w,h,p);p.color=Color.WHITE;p.textSize=58f;c.drawText("CRASH!",w*.28f,h*.42f,p);p.textSize=32f;c.drawText("Score: $score",w*.34f,h*.49f,p);c.drawText("Tap to restart",w*.28f,h*.57f,p)}
  override fun onTouchEvent(e:MotionEvent):Boolean{
   if(e.action==MotionEvent.ACTION_DOWN){
    if(state==0){reset();return true}
    if(state==2){reset();return true}
   }
   if(state==1 && (e.action==MotionEvent.ACTION_DOWN||e.action==MotionEvent.ACTION_MOVE)){
    carX=((e.x/width)-.07f)/.86f;carX=carX.coerceIn(.17f,.83f);invalidate()
   }
   return true
  }
 }
}
