/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.zrp200.rkpd2.windows;

import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.hero.HeroClass;
import com.zrp200.rkpd2.items.KromerMask;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.scenes.PixelScene;
import com.zrp200.rkpd2.sprites.HeroSprite;
import com.zrp200.rkpd2.sprites.ItemSprite;
import com.zrp200.rkpd2.ui.*;

import java.util.ArrayList;

public class WndChooseClass extends Window {

	private static final int WIDTH		= 130;
	private static final float GAP		= 2;

	public WndChooseClass(final KromerMask tome, final Hero hero, final ArrayList<HeroClass> classes) {
		
		super();

		IconTitle titlebar = new IconTitle();
		titlebar.icon( new ItemSprite( tome.image(), null ) );
		titlebar.label( tome.name() );
		titlebar.setRect( 0, 0, WIDTH, 0 );
		add( titlebar );

		RenderedTextBlock message = PixelScene.renderTextBlock( 6 );
		message.text( Messages.get(tome.getClass(), "message"), WIDTH );
		message.setPos( titlebar.left(), titlebar.bottom() + GAP );
		add( message );

		float pos = message.bottom() + 3*GAP;

		for (HeroClass cls: classes) {
			RedButton btnCls = new RedButton(cls.unlockMsg(), 6) {
				private void resolve() {
					WndChooseClass.this.hide();
					tome.choose(cls);
				}

				@Override
				protected void onClick() {
					GameScene.show(new WndOptions(HeroSprite.avatar(cls, 6),
							Messages.titleCase(cls.title()),
							Messages.get(WndChooseClass.this, "are_you_sure"),
							Messages.get(WndChooseClass.this, "yes"),
							Messages.get(WndChooseClass.this, "no")) {
						@Override
						protected void onSelect(int index) {
							hide();
							if (index == 0 && WndChooseClass.this.parent != null) {
								resolve();
							}
						}
					});
				}
			};
			btnCls.leftJustify = true;
			btnCls.multiline = true;
			btnCls.setSize(WIDTH - 20, btnCls.reqHeight() + 2);
			btnCls.setRect(0, pos, WIDTH - 20, btnCls.reqHeight() + 2);
			add(btnCls);

			IconButton clsInfo = new IconButton(Icons.get(Icons.INFO)) {
				@Override
				protected void onClick() {
					GameScene.show(new WndInfoClass(cls));
				}
			};
			clsInfo.setRect(WIDTH - 20, btnCls.top() + (btnCls.height() - 20) / 2, 20, 20);
			add(clsInfo);

			pos = btnCls.bottom() + GAP;
		}
			resize(WIDTH, (int) (pos + 1));
	}

	@Override
	public void onBackPressed() {
	}
}
