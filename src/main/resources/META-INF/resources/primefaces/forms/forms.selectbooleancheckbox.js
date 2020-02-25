/**
 * PrimeFaces SelectBooleanCheckbox Widget
 */
PrimeFaces.widget.SelectBooleanCheckbox = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.input = $(this.jqId + '_input');
        this.box = this.jq.find('.ui-chkbox-box');
        this.icon = this.box.children('.ui-chkbox-icon');
        this.itemLabel = this.jq.find('.ui-chkbox-label');
        this.disabled = this.input.is(':disabled');

        var $this = this;

        //bind events if not disabled
        if(!this.disabled) {
            this.box.on('mouseover.selectBooleanCheckbox', function() {
                $this.box.addClass('ui-state-hover');
            })
            .on('mouseout.selectBooleanCheckbox', function() {
                $this.box.removeClass('ui-state-hover');
            })
            .on('click.selectBooleanCheckbox', function() {
                $this.input.trigger('click');
            });

            this.input.on('focus.selectBooleanCheckbox', function() {
                $this.box.addClass('ui-state-focus');
            })
            .on('blur.selectBooleanCheckbox', function() {
                $this.box.removeClass('ui-state-focus');
            })
            .on('change.selectBooleanCheckbox', function(e) {
                if($this.isChecked())
                    $this.box.addClass('ui-state-active').children('.ui-chkbox-icon').removeClass('ui-icon-blank').addClass('ui-icon-check');
                else
                    $this.box.removeClass('ui-state-active').children('.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');
            });

            //toggle state on label click
            this.itemLabel.click(function() {
                $this.toggle();
                $this.input.trigger('focus');
            });
        }

        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

    toggle: function() {
    	if (!this.disabled) { // add check to do nothing in disabled state
	        if(this.isChecked())
	            this.uncheck();
	        else
	            this.check();
    	}
    },

    isChecked: function() {
        return this.input.prop('checked');
    },

    check: function() {
        if(!this.isChecked()) {
            this.input.prop('checked', true).trigger('change');
            this.input.attr('aria-checked', true);
            this.box.addClass('ui-state-active').children('.ui-chkbox-icon').removeClass('ui-icon-blank').addClass('ui-icon-check');
        }
    },

    uncheck: function() {
        if(this.isChecked()) {
            this.input.prop('checked', false).trigger('change');
            this.input.attr('aria-checked', false);
            this.box.removeClass('ui-state-active').children('.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');
        }
    },

    enable : function() {
		this.input.removeAttr("disabled");
		this.jq.removeClass("ui-state-disabled");

		this__turnOffAndInit(this);
	},

	disable : function() {
		this.input.attr("disabled","disabled");
		this.jq.addClass("ui-state-disabled");

		this.uncheck();
		this.__turnOffAndInit(this);
	},

	__turnOffAndInit: function(elem){
		elem.jq.off("remove");
		elem.box.off();
		elem.input.off();
		elem.itemLabel.off();
		elem.init(elem.cfg);
	}

});
