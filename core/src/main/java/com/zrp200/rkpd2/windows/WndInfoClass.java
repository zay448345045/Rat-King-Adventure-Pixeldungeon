package com.zrp200.rkpd2.windows;

import com.zrp200.rkpd2.actors.hero.HeroClass;
import com.zrp200.rkpd2.actors.hero.Talent;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.sprites.HeroSprite;
import com.zrp200.rkpd2.ui.TalentsPane;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static com.zrp200.rkpd2.ui.TalentButton.Mode.INFO;

public class WndInfoClass extends WndTitledMessage {

	public WndInfoClass(HeroClass cls){
		super( HeroSprite.avatar(cls, 6), Messages.titleCase(cls.title()), cls.desc(), WIDTH_MIN);

		Talent.trolling = true;
		ArrayList<LinkedHashMap<Talent, Integer>> talents = new ArrayList<>();
		Talent.initSecondClassTalents(cls, talents, new LinkedHashMap<>());

		TalentsPane.TalentTierPane talentPane = new TalentsPane.TalentTierPane(talents.get(2), 3, INFO);
		talentPane.title.text( Messages.titleCase(Messages.get(WndHeroInfo.class, "talents")));
		addToBottom(talentPane, 5, 0);
	}

	@Override
	public void onBackPressed() {
		Talent.trolling = false;
		super.onBackPressed();
	}
}
